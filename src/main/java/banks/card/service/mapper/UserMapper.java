package banks.card.service.mapper;

import banks.card.dto.in.user.UserCreateRequest;
import banks.card.dto.in.user.UserUpdateRequest;
import banks.card.dto.out.user.ListUserResponse;
import banks.card.dto.out.user.UserResponse;
import banks.card.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User createRequestToEntity(UserCreateRequest request);

    User updateRequestToEntity(UserUpdateRequest request);

    UserResponse entityToResponse(User user);

    default ListUserResponse listUserToListResponse(Page<User> users) {
        ListUserResponse response = new ListUserResponse();
        response.setUsers(users.stream()
                .map(this::entityToResponse)
                .toList());
        return response;
    }
}
