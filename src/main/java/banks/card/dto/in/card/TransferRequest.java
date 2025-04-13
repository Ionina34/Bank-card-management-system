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
@Schema(defaultValue = "Запрос на перевод средств между счетами")
public class TransferRequest {

    @Schema(defaultValue = "Идентификатор счета-источника", example = "2")
    @NotNull(message = "Идентификатор не может быть null")
    @Positive(message = "Идентификатор не может быть меньше 0")
    private Long fromCardId;

    @Schema(defaultValue = "Идентификатор счета-получателя", example = "1")
    @NotNull(message = "Идентификатор не может быть null")
    @Positive(message = "Идентификатор не может быть меньше 0")
    private Long toCardId;


    @Schema(defaultValue = "Сумма перевода", example = "200")
    @NotNull(message = "Сумма не может быть null")
    @Positive(message = "Сумма не может быть меньше 0")
    private BigDecimal amount;
}
