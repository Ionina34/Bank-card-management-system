package banks.card.dto.in.filter;

import banks.card.entity.TransactionType;
import banks.card.entity.TransferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "")
public class TransactionFilterRequest {

    @Schema(description = "Тип требуемой транзакции", example = "WITHDRAWAL || DEPOSIT || TRANSFER_OUT || TRANSFER_IN")
    private TransactionType type;

    @Schema(description = "Статус перемещения средств", example = "SUCCESS || FAILED || DECLINED")
    private TransferStatus status;

    @Schema(description = "Минимальная требуемая сумма транзакции", example = "150")
    @Positive(message = "Сумма транзакции не может быть меньше 0")
    private BigDecimal minAmount;

    @Schema(description = "Максимальная требуемая сумма транзакции", example = "500")
    @Positive(message = "Сумма транзакции не иодет быть меньше 0")
    private BigDecimal maxAmount;

    @Schema(description = "Нижняя граница даты", example = "2025-12-31")
    private Timestamp dateFrom;

    @Schema(description = "Верхняя граница даты", example = "2025-03-01")
    private Timestamp dateTo;
}
