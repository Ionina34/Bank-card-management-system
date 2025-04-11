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
@Schema(description = "Запрос на обновлвение информации о пользователе")
public class UserUpdateRequest {

    @Schema(description = "Email пользователя", example = "Jon@mail.ru")
    @Size(min = 5, max = 50, message = "Email должно содержать от 5 до 50 символов")
    @NotBlank(message = "Email пользователя не может быть пустным")
    @Email(message = "Email адрес должен быть в формате user@mail.ru")
    private String email;

    @Schema(description = "Статус карты", example = "ACTIVE")
    @NotBlank(message = "Статус не может быть пустым")
    private Role role;
}
