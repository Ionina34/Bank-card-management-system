package banks.card.dto.in.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на установку лимитов для карты")
public class UpdateCardLimitRequest {

    @Schema(description = "Дневной лимит", example = "150.0")
    @Min(value = 0, message = "Лимит должен быть больше 0")
    private BigDecimal dailyLimit;

    @Schema(description = "Месячный лимит",example = "3303.00")
    @Min(value = 0, message = "Лимит должен быть больше 0")
    private BigDecimal monthlyLimit;

    @Schema(description = "Лимит на одну транзакцию",example = "10000")
    @Min(value = 0, message = "Лимит должен быть больше 0")
    private BigDecimal singleTransactionLimit;

    @Schema(description = "Лимит на кол-во транзакция в день", example = "15")
    @Min(value = 0, message = "Лимит должен быть больше 0")
    private Integer dailyTransactionCountLimit;
}
