package banks.card.dto.in.user;

import banks.card.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на созлание пользователя")
public class UserCreateRequest {

    @Schema(description = "Email пользователя", example = "Jon")
    @Size(min = 5, max = 50, message = "Email должно содержать от 5 до 50 символов")
    @NotBlank(message = "Email пользователя не может быть пустным")
    @Email(message = "Email адрес должен быть в формате user@mail.ru")
    private String email;

    @Schema(description = "Пароль", example = "password1235")
    @Size(min = 8, max = 255, message = "Длина пароля должно быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @Schema(description = "Статус карты", example = "BLOCKED")
    @NotBlank(message = "Статус не может быть пустым")
    private Role role;
}
