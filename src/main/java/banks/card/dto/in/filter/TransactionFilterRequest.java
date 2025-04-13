package banks.card.dto.in.filter;

import banks.card.entity.TransactionType;
import banks.card.entity.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFilterRequest {

    private TransactionType type;
    private TransferStatus status;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Timestamp dateFrom;
    private Timestamp dateTo;
}
