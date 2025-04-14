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
@Schema(description = "Объект ответа, содержащий информацию о результате перемещения средств")
public class TransferResponse {

    @Schema(description = "Идентификатор карты-источника", example = "2")
    private Long fromCardId;

    @Schema(description = "Идентификатор карты-получатель", example = "54")
    private Long toCardId;

    @Schema(description = "Сумма средст", example = "1500")
    private BigDecimal amount;

    @Schema(description = "Дата", example = "2025-04-03")
    private Timestamp date;

    @Schema(description = "Статус перемещения средств", example = "SUCCESS")
    private TransferStatus status;
}
