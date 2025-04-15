package banks.card.service.Impl.admin;

import banks.card.dto.in.user.UserCreateRequest;
import banks.card.dto.in.user.UserPasswordResetRequest;
import banks.card.dto.in.user.UserUpdateRequest;
import banks.card.dto.out.MessageResponse;
import banks.card.dto.out.user.ListUserResponse;
import banks.card.dto.out.user.UserResponse;
import banks.card.entity.Role;
import banks.card.entity.User;
import banks.card.exception.EntityNotFoundException;
import banks.card.repository.UserRepository;
import banks.card.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAdminServiceImpl userAdminService;

    private User user;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;
    private UserPasswordResetRequest passwordResetRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("encoded-password");
        user.setRole(Role.ROLE_USER);

        createRequest = new UserCreateRequest();
        createRequest.setEmail("user@example.com");
        createRequest.setPassword("plain-password");
        createRequest.setRole(Role.ROLE_USER);

        updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("updated@example.com");

        passwordResetRequest = new UserPasswordResetRequest();
        passwordResetRequest.setNewPassword("new-password");

        pageable = (Pageable) PageRequest.of(0, 10);
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userAdminService.findById(1L);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userAdminService.findById(1L);
        });
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository).findById(1L);
    }

    @Test
    void testCreate_Success() {
        when(userMapper.createRequestToEntity(createRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        UserResponse response = new UserResponse();
        when(userMapper.entityToResponse(user)).thenReturn(response);

        UserResponse result = userAdminService.create(createRequest);

        assertEquals(response, result);
        assertEquals("encoded-password", user.getPassword());
        verify(userMapper).createRequestToEntity(createRequest);
        verify(passwordEncoder).encode("plain-password");
        verify(userRepository).save(user);
        verify(userMapper).entityToResponse(user);
    }

    @Test
    void testGetAll_NoRoleFilter_Success() {
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        ListUserResponse response = new ListUserResponse();
        when(userMapper.listUserToListResponse(userPage)).thenReturn(response);

        ListUserResponse result = userAdminService.getAll(pageable, null);

        assertEquals(response, result);
        verify(userRepository).findAll(pageable);
        verify(userMapper).listUserToListResponse(userPage);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetAll_WithRoleFilter_Success() {
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findByRole(Role.ROLE_USER, pageable)).thenReturn(userPage);
        ListUserResponse response = new ListUserResponse();
        when(userMapper.listUserToListResponse(userPage)).thenReturn(response);

        ListUserResponse result = userAdminService.getAll(pageable, "ROLE_USER");

        assertEquals(response, result);
        verify(userRepository).findByRole(Role.ROLE_USER, pageable);
        verify(userMapper).listUserToListResponse(userPage);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetAll_WithInvalidRole_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userAdminService.getAll(pageable, "INVALID_ROLE");
        });
        assertTrue(exception.getMessage().contains("No enum constant"));
        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void testUpdate_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");
        when(userMapper.updateRequestToEntity(updateRequest)).thenReturn(updatedUser);
        when(userRepository.save(user)).thenReturn(user);
        UserResponse response = new UserResponse();
        when(userMapper.entityToResponse(user)).thenReturn(response);

        UserResponse result = userAdminService.update(1L, updateRequest);

        assertEquals(response, result);
        verify(userRepository).findById(1L);
        verify(userMapper).updateRequestToEntity(updateRequest);
        verify(userRepository).save(user);
        verify(userMapper).entityToResponse(user);
    }

    @Test
    void testUpdate_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userAdminService.update(1L, updateRequest);
        });
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository).findById(1L);
        verifyNoInteractions(userMapper);
    }

    @Test
    void testPasswordReset_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");
        when(userRepository.save(user)).thenReturn(user);

        MessageResponse result = userAdminService.passwordReset(1L, passwordResetRequest);

        assertEquals("Password reset successfully", result.getMessage());
        assertEquals("encoded-new-password", user.getPassword());
        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("new-password");
        verify(userRepository).save(user);
    }

    @Test
    void testPasswordReset_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userAdminService.passwordReset(1L, passwordResetRequest);
        });
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository).findById(1L);
        verifyNoInteractions(passwordEncoder, userMapper);
    }

    @Test
    void testDelete_Success() {
        userAdminService.delete(1L);

        verify(userRepository).deleteById(1L);
    }
}
