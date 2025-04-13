package banks.card.dto.out.transaction;

import banks.card.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private Timestamp transactionDate;
    private String description;
}
