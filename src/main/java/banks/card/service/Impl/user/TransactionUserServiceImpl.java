package banks.card.service.Impl.user;

import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.entity.Card;
import banks.card.entity.Transaction;
import banks.card.entity.TransactionType;
import banks.card.entity.TransferStatus;
import banks.card.exception.EntityNotFoundException;
import banks.card.repository.TransactionRepository;
import banks.card.service.aspect.CheckingRightsCard;
import banks.card.service.mapper.TransactionMapper;
import banks.card.service.services.user.CardUserActionService;
import banks.card.service.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionUserServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardUserActionService cardService;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction createAndSave(Card fromCard, Card toCard, BigDecimal amount, TransferStatus status, TransactionType type, String message) {
        Transaction transaction = Transaction.createTransaction(fromCard, toCard, amount,
                status, type, message);
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction createAndSave(Card fromCard, BigDecimal amount, TransferStatus status, TransactionType type, String message) {
        Transaction transaction = Transaction.createTransaction(fromCard, amount,
                status, type, message);
        return transactionRepository.save(transaction);
    }

    @Override
    @CheckingRightsCard(cardIdIndex = 0, tokenIdIndex = 1)
    public ListTransactionResponse getUserTransactions(Long cardId, String token, Pageable pageable)
            throws EntityNotFoundException {
        Card card = cardService.findById(cardId);
        Page<Transaction> transactions = transactionRepository.findByCard(card, pageable);
        return transactionMapper.listEntityToResponseEntity(transactions);
    }

    @Override
    public ListTransactionResponse getCardTransactions(Long cardId, Pageable pageable)
            throws EntityNotFoundException {
        Card card = cardService.findById(cardId);
        Page<Transaction> transactions = transactionRepository.findByCard(card, pageable);
        return transactionMapper.listEntityToResponseEntity(transactions);
    }

    @Override
    public List<Transaction> findByCardAndTransactionDateAfterAndTypeIn(Card card, Timestamp date, List<TransactionType> types) {
        return transactionRepository.findByCardAndTransactionDateAfterAndTransactionTypeIn(card, date, types);
    }

    @Override
    public long countByCardAndTransactionDateAfterAndTransactionTypeIn(Card card, Timestamp timestamp, List<TransactionType> types) {
        return transactionRepository.countByCardAndTransactionDateAfterAndTransactionTypeIn(card, timestamp, types);
    }
}
