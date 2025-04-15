package banks.card.web.controller;

import banks.card.dto.in.SignInUpRequest;
import banks.card.dto.out.JwtAuthenticationResponse;
import banks.card.exception.EntityExistsException;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest extends AbstractControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    private SignInUpRequest signInUpRequest;
    private JwtAuthenticationResponse response;

    @BeforeEach
    public void init() {
        response = new JwtAuthenticationResponse("mock-jwt-token");
        signInUpRequest = new SignInUpRequest("email@mail.ru", "password");

        MockitoAnnotations.openMocks(this);
        setupMockMvc(authController);
    }

    @Test
    void signUp_ValidRequest_ReturnsAccepted() throws Exception {
        when(authenticationService.signUp(any(SignInUpRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInUpRequest)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));

        verify(authenticationService, times(1)).signUp(any(SignInUpRequest.class));
    }

    @Test
    void signUp_InvalidRequest_ReturnsBadRequest() throws Exception {
        SignInUpRequest invalidRequest = new SignInUpRequest();

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());

        verify(authenticationService, never()).signUp(any(SignInUpRequest.class));
    }

    @Test
    void signUp_ExistingUser_ReturnsConflict() throws Exception {
        when(authenticationService.signUp(any(SignInUpRequest.class)))
                .thenThrow(new EntityExistsException("User already exists"));

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInUpRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User already exists"));

        verify(authenticationService, times(1)).signUp(any(SignInUpRequest.class));
    }

    @Test
    void signIn_ValidRequest_ReturnsAccepted() throws Exception {
        when(authenticationService.signIn(any(SignInUpRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInUpRequest)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));

        verify(authenticationService, times(1)).signIn(any(SignInUpRequest.class));
    }

    @Test
    void signIn_InvalidRequest_ReturnsBadRequest() throws Exception {
        SignInUpRequest invalidRequest = new SignInUpRequest();

        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());

        verify(authenticationService, never()).signIn(any(SignInUpRequest.class));
    }

    @Test
    void signIn_UserNotFound_ReturnsNotFound() throws Exception {
        when(authenticationService.signIn(any(SignInUpRequest.class)))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInUpRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(authenticationService, times(1)).signIn(any(SignInUpRequest.class));
    }
}
