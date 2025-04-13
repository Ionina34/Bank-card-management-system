package banks.card.dto.out.card;

import banks.card.dto.out.transaction.TransactionResponse;
import banks.card.entity.CardStatus;
import banks.card.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {

    private Long id;
    private String encryptedCardNumber;
    private String cardHolder;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
    private BigDecimal singleTransactionLimit;
    private Integer dailyTransactionCountLimit;
    private Long userId;
    private List<TransactionResponse> transactions;
}
