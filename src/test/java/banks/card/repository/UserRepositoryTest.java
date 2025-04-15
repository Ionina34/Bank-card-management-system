package banks.card.repository;

import banks.card.entity.Role;
import banks.card.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest extends AbstractRepositoryTest{

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = Role.ROLE_USER;
        user = User.builder()
                .email("test@example.com")
                .password("password")
                .role(userRole)
                .build();
        userRepository.save(user);

        User adminUser = User.builder()
                .email("admin@example.com")
                .password("password")
                .role(Role.ROLE_ADMIN)
                .build();
        userRepository.save(adminUser);
    }

    @Test
    void findByEmail_WhenEmailExists_ReturnsUser() {
        Optional<User> result = userRepository.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    void findByEmail_WhenEmailDoesNotExist_ReturnsEmpty() {
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        assertThat(result).isNotPresent();
    }

    @Test
    void existsByEmail_WhenEmailExists_ReturnsTrue() {
        boolean exists = userRepository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_WhenEmailDoesNotExist_ReturnsFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void findByRole_WithPagination_ReturnsCorrectPage() {
        Pageable pageable = PageRequest.of(0, 1);

        Page<User> result = userRepository.findByRole(userRole, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("test@example.com");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
