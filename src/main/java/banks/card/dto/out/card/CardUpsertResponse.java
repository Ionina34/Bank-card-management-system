package banks.card.dto.out.card;

import banks.card.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с основной информацией о созданой карте")
public class CardUpsertResponse {

    @Schema(description = "Идентифиактор карты")
    private Long id;

    @Schema(description = "Владелец карты")
    private String cardHolder;

    @Schema(description = "Статус карты")
    private CardStatus status;
}
