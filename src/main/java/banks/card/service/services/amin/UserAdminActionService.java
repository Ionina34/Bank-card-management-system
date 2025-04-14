package banks.card.service.services.amin;

import banks.card.dto.in.user.UserCreateRequest;
import banks.card.dto.in.user.UserPasswordResetRequest;
import banks.card.dto.in.user.UserUpdateRequest;
import banks.card.dto.out.MessageResponse;
import banks.card.dto.out.user.ListUserResponse;
import banks.card.dto.out.user.UserResponse;
import banks.card.entity.User;
import banks.card.exception.EntityNotFoundException;
import org.springframework.data.domain.Pageable;

/**
 * Сервис для административных действий с пользователями.
 * Предоставляет методы для создания, обновления, удаления, поиска пользователей,
 * а также сброса пароля.
 */
public interface UserAdminActionService {

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект {@link User}, соответствующий указанному идентификатору
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    User findById(Long id);

    /**
     * Создает нового пользователя на основе переданного запроса.
     *
     * @param request объект {@link UserCreateRequest} с данными для создания пользователя
     * @return объект {@link UserResponse} с данными созданного пользователя
     */
    UserResponse create(UserCreateRequest request);

    /**
     * Возвращает список пользователей с учетом пагинации и фильтрации по роли.
     *
     * @param pageable объект {@link Pageable} для настройки пагинации
     * @param role     строка, указывающая роль пользователя для фильтрации (опционально)
     * @return объект {@link ListUserResponse}, содержащий список пользователей
     */
    ListUserResponse getAll(Pageable pageable, String role);

    /**
     * Обновляет данные пользователя на основе переданного запроса.
     *
     * @param userId  идентификатор пользователя
     * @param request объект {@link UserUpdateRequest} с данными для обновления
     * @return объект {@link UserResponse} с обновленными данными пользователя
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    UserResponse update(Long userId, UserUpdateRequest request);

    /**
     * Сбрасывает пароль пользователя.
     *
     * @param userId  идентификатор пользователя
     * @param request объект {@link UserPasswordResetRequest} с новым паролем
     * @return объект {@link MessageResponse} с сообщением об успешном сбросе пароля
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    MessageResponse passwordReset(Long userId, UserPasswordResetRequest request);

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     */
    void delete(Long userId);
}
