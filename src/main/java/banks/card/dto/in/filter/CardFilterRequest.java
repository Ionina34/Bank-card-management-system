package banks.card.dto.in.filter;

import banks.card.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на фильтрацию карт")
public class CardFilterRequest {

    @Schema(description = "Частичное совпадение номера карты", example = "123")
    @Size(max = 19, message = "Номер карты не может содердать больше 19 симовлов, 16 цифр и 3 пробела")
    private String number;

    @Schema(description = "Нужный статус карты", example = "ACTIVE || BLOCKED || EXPIRED")
    private CardStatus status;

    @Schema(description = "Минимальный требуемый баланс карты", example = "300")
    @Min(value = 0,message = "")
    private BigDecimal minBalance;

    @Schema(description = "Максимвльный требуемый баланс карты", example = "5000")
    @Min(value = 0, message = "")
    private BigDecimal maxBalance;
}
