package banks.card.service.mapper;

import banks.card.dto.in.user.UserCreateRequest;
import banks.card.dto.in.user.UserUpdateRequest;
import banks.card.dto.out.user.ListUserResponse;
import banks.card.dto.out.user.UserResponse;
import banks.card.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

/**
 * Маппер для преобразования DTO в сущности {@link User} и обратно.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Преобразует запрос на создание пользователя {@link UserCreateRequest} в сущность {@link User}.
     *
     * @param request запрос на создание пользователя
     * @return сущность {@link User}
     */
    User createRequestToEntity(UserCreateRequest request);

    /**
     * Преобразует запрос на обновление пользователя {@link UserUpdateRequest} в сущность {@link User}.
     *
     * @param request запрос на обновление пользователя
     * @return сущность {@link User}
     */
    User updateRequestToEntity(UserUpdateRequest request);

    /**
     * Преобразует сущность {@link User} в объект ответа {@link UserResponse}.
     *
     * @param user сущность пользователя
     * @return объект ответа {@link UserResponse}
     */
    UserResponse entityToResponse(User user);

    /**
     * Преобразует страницу сущностей {@link User} в объект ответа {@link ListUserResponse}.
     *
     * @param users страница пользователей
     * @return объект ответа {@link ListUserResponse}, содержащий список преобразованных пользователей
     */
    default ListUserResponse listUserToListResponse(Page<User> users) {
        ListUserResponse response = new ListUserResponse();
        response.setUsers(users.stream()
                .map(this::entityToResponse)
                .toList());
        return response;
    }
}
