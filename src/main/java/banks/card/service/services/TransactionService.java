package banks.card.service.services;

import banks.card.dto.in.filter.TransactionFilterRequest;
import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.entity.Card;
import banks.card.entity.Transaction;
import banks.card.entity.TransactionType;
import banks.card.entity.TransferStatus;
import banks.card.service.aspect.CheckingRightsCard;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface TransactionService {

    Transaction createAndSave(Card fromCard, Card toCard, BigDecimal amount, TransferStatus status,
                              TransactionType type, String message);

    Transaction createAndSave(Card fromCard, BigDecimal amount, TransferStatus status,
                              TransactionType type, String message);

    @CheckingRightsCard
    ListTransactionResponse getUserTransactions(Long cardId, String token, TransactionFilterRequest filter, Pageable pageable);

    ListTransactionResponse getCardTransactions(Long cardId, TransactionFilterRequest filter, Pageable pageable);

    List<Transaction> findByCardAndTransactionDateAfterAndTypeIn(Card card, Timestamp date, List<TransactionType> types);

    long countByCardAndTransactionDateAfterAndTransactionTypeIn(Card card, Timestamp timestamp, List<TransactionType> types);
}
