package banks.card.dto.in.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(defaultValue = "Запрос на снятие средств с карты")
public class WithdrawalRequest {

    @Schema(description = "Идентификатор карты", example = "123")
    @NotNull(message = "Идентификатор не моежет быть null")
    private Long cardId;

    @Schema(description = "Сумма для снятия средств",example = "150")
    @Positive
    private BigDecimal amount;
}
