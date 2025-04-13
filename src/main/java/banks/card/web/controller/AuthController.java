package banks.card.web.controller;

import banks.card.dto.in.SignInUpRequest;
import banks.card.dto.out.error.ErrorMessageResponse;
import banks.card.dto.out.JwtAuthenticationResponse;
import banks.card.exception.EntityExistsException;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация пользователя")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(
            @Parameter(description = "Данные для регистрации") @RequestBody @Valid SignInUpRequest request) {
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

    @Operation(summary = "Аутентификация пользователя")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(
            @Parameter(description = "Данные для аутентификации") @RequestBody @Valid SignInUpRequest request) {
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
