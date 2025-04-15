package banks.card.service.Impl.security;

import banks.card.dto.in.SignInUpRequest;
import banks.card.dto.out.JwtAuthenticationResponse;
import banks.card.entity.Role;
import banks.card.entity.User;
import banks.card.exception.EntityExistsException;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.security.AuthenticationService;
import banks.card.service.security.JwtService;
import banks.card.service.services.user.UserUserActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserUserActionService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private SignInUpRequest request;
    private User user;
    private final String jwtToken = "mocked-jwt-token";

    @BeforeEach
    void setUp() {
        request = new SignInUpRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void signUp_Successful_ReturnsJwtToken() {
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userService.create(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn(jwtToken);

        JwtAuthenticationResponse response = authenticationService.signUp(request);

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        verify(userService).create(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void signUp_UserAlreadyExists_ThrowsEntityExistsException() {
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        doThrow(new EntityExistsException("User already exists")).when(userService).create(any(User.class));

        assertThrows(EntityExistsException.class, () -> authenticationService.signUp(request));
        verify(userService).create(any(User.class));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void signIn_Successful_ReturnsJwtToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(user);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(jwtToken);

        JwtAuthenticationResponse response = authenticationService.signIn(request);

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(request.getEmail());
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void signIn_UserNotFound_ThrowsEntityNotFoundException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(request.getEmail()))
                .thenThrow(new EntityNotFoundException("User not found"));

        assertThrows(EntityNotFoundException.class, () -> authenticationService.signIn(request));
        verify(userDetailsService).loadUserByUsername(request.getEmail());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void signIn_InvalidCredentials_ThrowsAuthenticationException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Invalid credentials") {});

        assertThrows(org.springframework.security.core.AuthenticationException.class,
                () -> authenticationService.signIn(request));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never()).userDetailsService();
        verify(jwtService, never()).generateToken(any());
    }
}
