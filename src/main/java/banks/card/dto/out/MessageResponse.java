package banks.card.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект ответа, содержащий сообщение о результате запроса")
public class MessageResponse {

    @Schema(description = "Сообщение с информацией",example = "The password has been reset")
    private String message;
}
