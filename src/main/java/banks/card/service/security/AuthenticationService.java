package banks.card.service.security;

import banks.card.dto.in.SignInUpRequest;
import banks.card.dto.out.JwtAuthenticationResponse;
import banks.card.entity.Role;
import banks.card.entity.User;
import banks.card.exception.EntityExistsException;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.services.user.UserUserActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для регистрации и аутентификации пользователей.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserUserActionService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрирует нового пользователя и возвращает JWT-токен.
     *
     * @param request данные для регистрации пользователя
     * @return объект {@link JwtAuthenticationResponse} с JWT-токеном
     * @throws EntityExistsException если пользователь с указанным email уже существует
     */
    public JwtAuthenticationResponse signUp(SignInUpRequest request) throws EntityExistsException {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.create(user);

        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентифицирует пользователя и возвращает JWT-токен.
     *
     * @param request данные для аутентификации пользователя
     * @return объект {@link JwtAuthenticationResponse} с JWT-токеном
     * @throws EntityNotFoundException если пользователь с указанным email не найден
     */
    public JwtAuthenticationResponse signIn(SignInUpRequest request) throws EntityNotFoundException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        UserDetails user = userService
                .userDetailsService()
                .loadUserByUsername(request.getEmail());

        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}
