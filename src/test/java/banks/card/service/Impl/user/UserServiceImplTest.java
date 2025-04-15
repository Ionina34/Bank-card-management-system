package banks.card.service.Impl.user;

import banks.card.entity.User;
import banks.card.exception.EntityExistsException;
import banks.card.exception.EntityNotFoundException;
import banks.card.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private String email;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        user = new User();
        user.setId(1L);
        user.setEmail(email);
    }

    @Test
    void testSave_Success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.save(user);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testCreate_Success() {
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create(user);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testCreate_UserAlreadyExists() {
        when(userRepository.existsByEmail(email)).thenReturn(true);

        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> {
            userService.create(user);
        });

        assertEquals("User already exists with Email: " + email, exception.getMessage());
        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testFindByEmail_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.findByEmail(email);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testFindByEmail_NotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.findByEmail(email);
        });

        assertEquals("User not found with Email: " + email, exception.getMessage());
        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUserDetailsService_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = userService.userDetailsService();
        User result = (User) userDetailsService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUserDetailsService_NotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = userService.userDetailsService();
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        assertEquals("User not found with Email: " + email, exception.getMessage());
        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }
}
