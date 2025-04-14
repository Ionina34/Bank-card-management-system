package banks.card.dto.out.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект ответа, содержащей информацию об опльзоватле")
public class UserResponse {

    @Schema(description = "Идентификатор пользователя", example = "5")
    private Long id;

    @Schema(description = "Электронная почта пользователя", example = "email@,ail.ru")
    private String email;

    @Schema(description = "Роль пользователя в системе",example = "ROLE_USER")
    private String role;
}
