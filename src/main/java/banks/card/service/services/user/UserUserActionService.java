package banks.card.service.services.user;

import banks.card.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserUserActionService {

    User findByEmail(String email);

    /**
     * Сохранение пользователя
     *
     * @param user Данны для сохранения
     * @return Сохраненный пользователь
     */
    User save(User user);

    /**
     * Создание нового пользователя
     *
     * @param user Данные для создания
     * @return Созданный пользователь
     */
    User create(User user);

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     * </p>
     *
     * @return пользователь
     */
    UserDetailsService userDetailsService();

}
