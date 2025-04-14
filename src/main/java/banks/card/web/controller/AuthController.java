package banks.card.web.controller;

import banks.card.dto.in.SignInUpRequest;
import banks.card.dto.out.error.ErrorMessageResponse;
import banks.card.dto.out.JwtAuthenticationResponse;
import banks.card.exception.EntityExistsException;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для аутентификации и регистрации пользователей.
 * Предоставляет методы для регистрации нового пользователя и входа в систему.
 */
@Tag(name = "Аутентификация пользователя", description = "API для регистрации и аутентификации пользователей")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Регистрирует нового пользователя.
     *
     * @param request объект с данными для регистрации (например, email и пароль)
     * @return объект {@link ResponseEntity} с {@link JwtAuthenticationResponse} при успешной регистрации
     * или {@link ErrorMessageResponse} при конфликте (например, пользователь уже существует)
     * @throws EntityExistsException если пользователь с указанными данными уже существует
     */
    @Operation(
            summary = "Регистрация пользователя",
            description = "Регистрирует нового пользователя на основе предоставленных данных. Возвращает JWT-токен при успехе.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Пользователь успешно зарегистрирован",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с указанными данными уже существует",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(
            @Parameter(description = "Данные для регистрации (например, email и пароль)", required = true)
            @RequestBody @Valid SignInUpRequest request) {
        try {
            JwtAuthenticationResponse response = authenticationService.signUp(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(response);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(
                            new ErrorMessageResponse(
                                    e.getMessage()
                            )
                    );
        }
    }

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param request объект с данными для входа (например, email и пароль)
     * @return объект {@link ResponseEntity} с {@link JwtAuthenticationResponse} при успешной аутентификации
     * или {@link ErrorMessageResponse} если пользователь не найден
     * @throws EntityNotFoundException если пользователь с указанными данными не найден
     */
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Аутентифицирует пользователя на основе предоставленных данных. Возвращает JWT-токен при успехе.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Пользователь успешно аутентифицирован",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанными данными не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
    })
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(
            @Parameter(description = "Данные для аутентификации (например, email и пароль)", required = true)
            @RequestBody @Valid SignInUpRequest request) {
        try {
            JwtAuthenticationResponse response = authenticationService.signIn(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            new ErrorMessageResponse(
                                    e.getMessage()
                            )
                    );
        }
    }
}