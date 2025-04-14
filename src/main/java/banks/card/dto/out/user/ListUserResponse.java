package banks.card.dto.out.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект ответа, содержащей информацию об пользователях")
public class ListUserResponse {

    @Schema(description = "Списко ползователей, с подробной информацией о каждом")
    private List<UserResponse> users;
}
