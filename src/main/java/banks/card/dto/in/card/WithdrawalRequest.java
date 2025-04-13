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
@Schema(defaultValue = "Запроя на снятие средств с карты")
public class WithdrawalRequest {

    @NotNull
    private Long cardId;

    @Positive
    private BigDecimal amount;
}
