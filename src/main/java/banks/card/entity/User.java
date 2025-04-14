package banks.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Класс {@code User} представляет сущность пользователя в системе.
 * Реализует интерфейс {@code UserDetails} для интеграции с Spring Security.
 * Содержит информацию о пользователе, такую как email, пароль, роль и список связанных карт.
 */
@Entity
@Builder
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Электронная почта пользователя, используется как имя пользователя.
     * Не может быть пустой, максимальная длина — 128 символов, должна быть уникальной.
     */
    @Column(name = "email", length = 128, nullable = false, unique = true)
    private String email;

    /**
     * Пароль пользователя.
     * Не может быть пустым, максимальная длина — 128 символов.
     */
    @Column(name = "password", length = 128, nullable = false)
    private String password;

    /**
     * Роль пользователя (например, ROLE_USER, ROLE_ADMIN).
     * Хранится как строка в базе данных.
     * Не может быть пустой.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * Список карт, принадлежащих пользователю.
     * Связь типа "один ко многим", с каскадным удалением и обновлением.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Card> cards;

    /**
     * Возвращает список ролей пользователя для Spring Security.
     *
     * @return коллекция объектов {@code GrantedAuthority}, содержащая роль пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Возвращает имя пользователя (email).
     *
     * @return email пользователя
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Указывает, не истёк ли срок действия учётной записи.
     *
     * @return {@code true} — учётная запись не истекла
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, не заблокирована ли учётная запись.
     *
     * @return {@code true} — учётная запись не заблокирована
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, не истёк ли срок действия учетных данных.
     *
     * @return {@code true} — учетные данные не истекли
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, активна ли учётная запись.
     *
     * @return {@code true} — учётная запись активна
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
