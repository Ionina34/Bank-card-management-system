package banks.card.service.services.amin;

import banks.card.dto.in.user.UserCreateRequest;
import banks.card.dto.in.user.UserPasswordResetRequest;
import banks.card.dto.in.user.UserUpdateRequest;
import banks.card.dto.out.MessageResponse;
import banks.card.dto.out.user.ListUserResponse;
import banks.card.dto.out.user.UserResponse;
import banks.card.entity.User;
import org.springframework.data.domain.Pageable;

public interface UserAdminActionService {

    User findById(Long id);

    UserResponse create(UserCreateRequest request);

    ListUserResponse getAll(Pageable pageable, String role);

    UserResponse update(Long userId, UserUpdateRequest request);

    MessageResponse passwordReset(Long userId, UserPasswordResetRequest request);

    void delete(Long userId);
}
