package banks.card.dto.out.card;

import banks.card.entity.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalResponse {

    private Long cardId;
    private BigDecimal amount;
    private Timestamp date;
    private TransferStatus status;
}
