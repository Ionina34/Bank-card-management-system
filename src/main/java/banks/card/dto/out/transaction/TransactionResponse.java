package banks.card.dto.out.transaction;

import banks.card.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект ответа содержащий информацию об транзакции")
public class TransactionResponse {

    @Schema(description = "Идентификатор транзакции", example = "15")
    private Long id;

    @Schema(description = "Идентификатор карты-источника", example = "36")
    private Long fromCardId;

    @Schema(description = "Идекнтификатор карты-получателя, если это перевод средств", example = "85")
    private Long toCardId;

    @Schema(description = "Сумма транзакции", example = "1233")
    private BigDecimal amount;

    @Schema(description = "Тип транзакции", example = "TRANSFER_OUT")
    private TransactionType transactionType;

    @Schema(description = "Дата транзакции", example = "2025-03-02")
    private Timestamp transactionDate;

    @Schema(description = "Описани транзакции, или об ошибке проихошедшей во время ее выполнения", example = "The transfer has been delivered")
    private String description;
}
