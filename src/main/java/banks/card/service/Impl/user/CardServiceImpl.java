package banks.card.service.Impl.user;

import banks.card.dto.in.card.TransferRequest;
import banks.card.dto.in.card.WithdrawalRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.card.TransferResponse;
import banks.card.dto.out.card.WithdrawalResponse;
import banks.card.entity.*;
import banks.card.exception.EntityNotFoundException;
import banks.card.exception.TransferException;
import banks.card.exception.WithdrawalException;
import banks.card.repository.CardRepository;
import banks.card.service.aspect.CheckingRightsCard;
import banks.card.service.aspect.CheckingRightsCards;
import banks.card.service.services.TransactionService;
import banks.card.service.services.user.CardUserActionService;
import banks.card.service.services.user.UserUserActionService;
import banks.card.service.mapper.CardMapper;
import banks.card.service.security.JwtService;
import banks.card.service.specification.CardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static banks.card.service.security.JwtService.BEARER_PREFIX;

/**
 * Реализация {@link CardUserActionService} сервиса для действий пользователя с картами.
 */
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardUserActionService {

    private final CardRepository cardRepository;
    private final UserUserActionService userService;
    private final JwtService jwtService;
    private final CardMapper cardMapper;

    @Autowired
    @Lazy
    private TransactionService transactionService;

    @Override
    public Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Card not found by ID: " + id));
    }

    @Override
    public ListCardResponse getCards(String token, CardFilterRequest filter, Pageable pageable)
            throws EntityNotFoundException {
        token = token.substring(BEARER_PREFIX.length());
        String email = jwtService.extractEmail(token);

        User user = userService.findByEmail(email);
        Specification<Card> spec = CardSpecification.filterCards(filter);

        return cardMapper.listEntityToListResponse(
                cardRepository.findByUser(user, spec, pageable)
        );
    }

    @Override
    @CheckingRightsCard(cardIdIndex = 0, tokenIdIndex = 1)
    @Transactional
    public CardResponse blockedCard(Long cardId, String token)
            throws EntityNotFoundException, IllegalStateException {
        Card card = findById(cardId);

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalStateException("Cannot block an expired card");
        }
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new IllegalStateException("Cannot block an expired card");
        }

        card.setStatus(CardStatus.BLOCKED);
        Card updateCard = cardRepository.save(card);

        return cardMapper.entityToResponse(updateCard);
    }

    @Override
    @CheckingRightsCards
    @Transactional
    public TransferResponse transfer(String token, TransferRequest request)
            throws AccessDeniedException, EntityNotFoundException, TransferException {
        TransferResponse response = cardMapper.transferRequestToTransferResponse(request);

        Card fromCard = findById(request.getFromCardId());
        Card toCard = findById(request.getToCardId());
        Transaction errorTransaction = null;

        try {
            if (request.getFromCardId().equals(request.getToCardId())) {
                String message = "Cannot transfer to the same card";
                errorTransaction = transactionService.createAndSave(fromCard, toCard, request.getAmount(), TransferStatus.DECLINED, TransactionType.TRANSFER_OUT, message);
                throw new TransferException(message, response);
            }

            if (!fromCard.getStatus().equals(CardStatus.ACTIVE) || !toCard.getStatus().equals(CardStatus.ACTIVE)) {
                String message = "Both cards must be  active";
                errorTransaction = transactionService.createAndSave(fromCard, toCard, request.getAmount(), TransferStatus.DECLINED, TransactionType.TRANSFER_OUT, message);
                throw new TransferException(message, response);
            }

            try {
                checkingLimitsOfCard(fromCard, request.getAmount());
            } catch (IllegalStateException e) {
                errorTransaction =
                        transactionService.createAndSave(fromCard, toCard, request.getAmount(),
                                TransferStatus.DECLINED, TransactionType.TRANSFER_OUT, e.getMessage());
                throw new TransferException(e.getMessage(), response);
            }

            fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
            toCard.setBalance(toCard.getBalance().add(request.getAmount()));
            cardRepository.save(fromCard);
            cardRepository.save(toCard);

            String messageWithdrawal = "Transfer between accounts";
            String messageDeposit = "Replenishment from another account";
            Transaction withdrawal = transactionService
                    .createAndSave(fromCard, toCard, request.getAmount(),
                            TransferStatus.SUCCESS, TransactionType.TRANSFER_OUT, messageWithdrawal);
            Transaction deposit = transactionService
                    .createAndSave(toCard, fromCard, request.getAmount(),
                            TransferStatus.SUCCESS, TransactionType.TRANSFER_IN, messageDeposit);

            response.setStatus(TransferStatus.SUCCESS);
        } catch (TransferException e) {
            response.setStatus(TransferStatus.DECLINED);
            throw e;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            response.setStatus(TransferStatus.FAILED);
            String message = "Internal server error " + e.getMessage();
            Transaction tx = transactionService.createAndSave(fromCard, toCard, request.getAmount(), TransferStatus.FAILED, TransactionType.TRANSFER_OUT, message);
            throw new TransferException(message, response);
        }
        return response;
    }

    @Override
    @CheckingRightsCard
    @Transactional
    public WithdrawalResponse withdraw(WithdrawalRequest request, String token)
            throws AccessDeniedException, EntityNotFoundException, WithdrawalException {
        WithdrawalResponse response = cardMapper.withdrawalRequestToWithdrawalResponse(request);

        Card card = findById(request.getCardId());
        Transaction transaction = null;

        try {
            if (!card.getStatus().equals(CardStatus.ACTIVE)) {
                response.setStatus(TransferStatus.DECLINED);
                String message = "Card is not active";
                transaction = transactionService.createAndSave(card, request.getAmount(), TransferStatus.DECLINED, TransactionType.WITHDRAWAL, message);
                throw new WithdrawalException(message, response);
            }

            try {
                checkingLimitsOfCard(card, request.getAmount());
            } catch (IllegalStateException e) {
                transaction = transactionService
                        .createAndSave(card, request.getAmount(), TransferStatus.DECLINED, TransactionType.WITHDRAWAL, e.getMessage());
                throw new WithdrawalException(e.getMessage(), response);
            }

            card.setBalance(card.getBalance().subtract(response.getAmount()));

            String message = "Withdrawal completed successfully";
            Transaction withdrawal =
                    transactionService.createAndSave(card, request.getAmount(), TransferStatus.SUCCESS, TransactionType.WITHDRAWAL, message);

            response.setStatus(TransferStatus.SUCCESS);
        } catch (WithdrawalException e) {
            response.setStatus(TransferStatus.DECLINED);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            response.setStatus(TransferStatus.FAILED);
            String message = "Internal server error " + e.getMessage();
            Transaction tx =
                    transactionService.createAndSave(card, request.getAmount(), TransferStatus.FAILED, TransactionType.WITHDRAWAL, message);
            throw new WithdrawalException(message, response);
        }

        return response;
    }

    /**
     * Рассчитывает сумму расходов по карте за текущий день.
     *
     * @param card объект {@link Card}
     * @return сумма расходов за день
     */
    private BigDecimal calculateDailySpent(Card card) {
        Timestamp startOfDay = Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        return transactionService
                .findByCardAndTransactionDateAfterAndTypeIn(card, startOfDay, List.of(TransactionType.WITHDRAWAL, TransactionType.TRANSFER_OUT))
                .stream()
                .filter(t -> t.getTransferStatus().equals(TransferStatus.SUCCESS))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Рассчитывает сумму расходов по карте за текущий месяц.
     *
     * @param card объект {@link Card}
     * @return сумма расходов за месяц
     */
    private BigDecimal calculateMonthlySpent(Card card) {
        Timestamp startOfDay = Timestamp.valueOf(LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS));
        return transactionService
                .findByCardAndTransactionDateAfterAndTypeIn(card, startOfDay, List.of(TransactionType.WITHDRAWAL, TransactionType.TRANSFER_OUT))
                .stream()
                .filter(t -> t.getTransferStatus().equals(TransferStatus.SUCCESS))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Подсчитывает количество транзакций по карте за текущий день.
     *
     * @param card объект {@link Card}
     * @return количество транзакций за день
     */
    private long countDailyTransactions(Card card) {
        Timestamp startOfDay = Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        return transactionService
                .countByCardAndTransactionDateAfterAndTransactionTypeIn(card, startOfDay, List.of(TransactionType.WITHDRAWAL, TransactionType.TRANSFER_OUT));
    }

    /**
     * Проверяет лимиты карты перед выполнением операции.
     *
     * @param fromCard объект {@link Card}, с которой выполняется операция
     * @param amount   сумма операции
     * @throws IllegalStateException если превышен один из лимитов
     */
    private void checkingLimitsOfCard(Card fromCard, BigDecimal amount)
            throws TransferException {

        if (fromCard.getBalance().compareTo(amount) < 0) {
            String message = "Insufficient balance on source card";
            throw new IllegalStateException(message);
        }

        if (fromCard.getSingleTransactionLimit() != null &&
                amount.compareTo(fromCard.getSingleTransactionLimit()) > 0) {
            String message = "Amount exceeds single transaction limit";
            throw new IllegalStateException(message);
        }

        if (fromCard.getDailyLimit() != null) {
            BigDecimal dailySpent = calculateDailySpent(fromCard);
            if (dailySpent.add(amount).compareTo(fromCard.getDailyLimit()) > 0) {
                String message = "Daily limit exceeded";
                throw new IllegalStateException(message);
            }
        }

        if (fromCard.getMonthlyLimit() != null) {
            BigDecimal monthlySpent = calculateMonthlySpent(fromCard);
            if (monthlySpent.add(amount).compareTo(fromCard.getMonthlyLimit()) > 0) {
                String message = "Monthly limit exceeded";
                throw new IllegalStateException(message);
            }
        }

        if (fromCard.getDailyTransactionCountLimit() != null) {
            long dailyTransactionsCount = countDailyTransactions(fromCard);
            if (dailyTransactionsCount >= fromCard.getDailyTransactionCountLimit()) {
                String message = "Daily transaction count limit exceeded";
                throw new IllegalStateException(message);
            }
        }
    }
}
