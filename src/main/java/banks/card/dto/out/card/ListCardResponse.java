package banks.card.dto.out.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Объект ответа, содержаций информацию о карты")
public class ListCardResponse {

    @Schema(description = "Список карт")
    private List<CardResponse> cards;
}
