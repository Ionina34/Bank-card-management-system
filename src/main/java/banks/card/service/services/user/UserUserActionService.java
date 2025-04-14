package banks.card.service.services.user;

import banks.card.entity.User;
import banks.card.exception.EntityExistsException;
import banks.card.exception.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Сервиса для действий пользователя с учетной записью.
 * Предоставляет методы для создания, поиска и сохранения пользователей,
 * а также интеграцию с системой аутентификации.
 */
public interface UserUserActionService {

    /**
     * Находит пользователя по его email.
     *
     * @param email email пользователя
     * @return объект {@link User}, соответствующий указанному email
     * @throws EntityNotFoundException если пользователь с указанным email не найден
     */
    User findByEmail(String email);

    /**
     * Сохраняет пользователя в репозитории.
     *
     * @param user объект {@link User} для сохранения
     * @return сохраненный объект {@link User}
     */
    User save(User user);

    /**
     * Создает нового пользователя, проверяя уникальность email.
     *
     * @param user объект {@link User} с данными нового пользователя
     * @return сохраненный объект {@link User}
     * @throws EntityExistsException если пользователь с таким email уже существует
     */
    User create(User user);

    /**
     * Предоставляет сервис для загрузки данных пользователя по email для аутентификации.
     *
     * @return объект {@link UserDetailsService}, использующий метод {@link #findByEmail(String)}
     */
    UserDetailsService userDetailsService();

}
