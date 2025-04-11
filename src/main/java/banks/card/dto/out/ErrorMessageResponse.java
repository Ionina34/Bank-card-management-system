package banks.card.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Информация об ошибке")
public class ErrorMessageResponse {

    @Schema(description = "Сообщение об ошибке произошедней во время выполнения")
    private String message;
}
