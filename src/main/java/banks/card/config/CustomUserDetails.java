package banks.card.config;

import banks.card.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Класс, представляющий данные пользователя для аутентификации в Spring Security.
 * Реализует интерфейс {@link UserDetails}, предоставляя информацию о пользователе,
 * такую как email, пароль и роль, на основе объекта {@link User}.
 */
@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    /**
     * Объект пользователя, содержащий данные для аутентификации.
     */
    private final User user;

    /**
     * Возвращает коллекцию ролей пользователя.
     * Роль извлекается из объекта {@link User} и преобразуется в {@link SimpleGrantedAuthority}
     * с префиксом "ROLE_".
     *
     * @return коллекция объектов {@link GrantedAuthority}, содержащая роль пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return строка с хэшированным паролем пользователя
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Возвращает имя пользователя (email).
     *
     * @return строка с email пользователя
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Указывает, не истек ли срок действия учетной записи пользователя.
     *
     * @return {@code true}, так как учетная запись всегда считается действующей
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, не заблокирована ли учетная запись пользователя.
     *
     * @return {@code true}, так как учетная запись всегда считается разблокированной
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, не истек ли срок действия учетных данных пользователя.
     *
     * @return {@code true}, так как учетные данные всегда считаются действующими
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, активна ли учетная запись пользователя.
     *
     * @return {@code true}, так как учетная запись всегда считается активной
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}