package banks.card.repository;

import banks.card.entity.Role;
import banks.card.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий {@code UserRepository} предоставляет методы для работы с сущностью {@code User} в базе данных.
 * Наследуется от {@code JpaRepository} для базовых операций CRUD.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по его электронной почте.
     *
     * @param email электронная почта пользователя
     * @return {@code Optional}, содержащий найденного пользователя, или пустой, если пользователь не найден
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверяет, существует ли пользователь с указанной электронной почтой.
     *
     * @param email электронная почта для проверки
     * @return {@code true}, если пользователь с такой почтой существует, иначе {@code false}
     */
    boolean existsByEmail(String email);

    /**
     * Находит пользователей с указанной ролью с поддержкой пагинации.
     *
     * @param role роль пользователей для фильтрации
     * @param pageable параметры пагинации и сортировки
     * @return страница с найденными пользователями, удовлетворяющими условиям
     */
    Page<User> findByRole(Role role, Pageable pageable);
}
