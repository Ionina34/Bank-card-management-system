package banks.card.dto.in.card;

import banks.card.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Запрос на обновление статуса карты")
public class CardUpdateStatusRequest {

    @Schema(description = "Статус карты", example = "ACTIVE")
    @NotBlank(message = "Статус не может быть пустым")
    private CardStatus status;
}
