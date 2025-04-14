package banks.card.dto.out.card;

import banks.card.entity.TransferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект ответа, содержащий информацию о снятии средств с карты")
public class WithdrawalResponse {

    @Schema(description = "Идентификатор карты", example = "5")
    private Long cardId;

    @Schema(description = "Сумма для снятия", example = "2500")
    private BigDecimal amount;

    @Schema(description = "Дата операции", example = "2025-03-03")
    private Timestamp date;

    @Schema(description = "Статус  перемещения средств", example = "DECLINE")
    private TransferStatus status;
}
