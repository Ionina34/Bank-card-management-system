package banks.card.dto.in.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на сброс пароля")
public class UserPasswordResetRequest {

    @Schema(description = "Пароль", example = "password1235")
    @Size(min = 8, max = 255, message = "Длина пароля должно быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String newPassword;
}
