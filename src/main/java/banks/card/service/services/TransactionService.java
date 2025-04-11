package banks.card.service.services;

import banks.card.dto.out.transaction.ListTransactionResponse;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    ListTransactionResponse getUserTransactions(Long cardId, String token, Pageable pageable);

    ListTransactionResponse getCardTransactions(Long cardId, Pageable pageable);
}
