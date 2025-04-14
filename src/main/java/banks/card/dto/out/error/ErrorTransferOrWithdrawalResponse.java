package banks.card.dto.out.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект ответа, содержащий информацию об ошибке возникшей во время обработки запроса перевода/снятии средст в карты")
public class ErrorTransferOrWithdrawalResponse<T> {

    @Schema(description = "Сообщение, с информацие об ошибке", example = "Card not active ")
    String message;

    @Schema(description = "Объект ответа, с результатом операции над балансом карты")
    T response;
}
