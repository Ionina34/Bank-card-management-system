package banks.card.web.controller.admin;

import banks.card.dto.in.user.UserCreateRequest;
import banks.card.dto.in.user.UserPasswordResetRequest;
import banks.card.dto.in.user.UserUpdateRequest;
import banks.card.dto.out.MessageResponse;
import banks.card.dto.out.user.ListUserResponse;
import banks.card.dto.out.user.UserResponse;
import banks.card.entity.Role;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.services.amin.UserAdminActionService;
import banks.card.web.controller.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserAdminControllerTest extends AbstractControllerTest {

    @Mock
    private UserAdminActionService adminActionService;

    @InjectMocks
    private UserAdminController adminController;

    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;
    private UserPasswordResetRequest passwordResetRequest;

    @BeforeEach
    public void init() {
        createRequest = new UserCreateRequest("email@mail.ru", "password", Role.ROLE_USER);
        updateRequest = new UserUpdateRequest("newemail@mail.ru", Role.ROLE_ADMIN);
        passwordResetRequest = new UserPasswordResetRequest("newPassword");

        MockitoAnnotations.openMocks(this);
        setupMockMvc(adminController);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_ValidRequest_ReturnsCreated() throws Exception {
        UserResponse response = new UserResponse(1L,"email@mail.ru", "ROLE_USER");
        when(adminActionService.create(any(UserCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("email@mail.ru"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

        verify(adminActionService, times(1)).create(any(UserCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        UserCreateRequest invalidRequest = new UserCreateRequest();

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists());

        verify(adminActionService, never()).create(any(UserCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUser_UnauthorizedRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        verify(adminActionService, never()).create(any(UserCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ValidRequest_ReturnsOk() throws Exception {
        ListUserResponse response = new ListUserResponse(List.of(new UserResponse(1L,"email@mail.ru", "ROLE_USER")));
        when(adminActionService.getAll(eq(PageRequest.of(0, 10)), eq("USER"))).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/users")
                        .param("role", "USER")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.users[0].id").value(1L))
                .andExpect(jsonPath("$.users.length()").value(1));

        verify(adminActionService, times(1)).getAll(eq(PageRequest.of(0, 10)), eq("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_ValidRequest_ReturnsOk() throws Exception {
        UserResponse response = new UserResponse(1L,"newemail@mail.ru", "ROLE_ADMIN");
        when(adminActionService.update(eq(1L), any(UserUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.email").value("newemail@mail.ru"));

        verify(adminActionService, times(1)).update(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_NotFound_ReturnsNotFound() throws Exception {
        when(adminActionService.update(eq(1L), any(UserUpdateRequest.class)))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(put("/api/v1/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(adminActionService, times(1)).update(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void passwordReset_ValidRequest_ReturnsOk() throws Exception {
        MessageResponse messageResponse = new MessageResponse("Password reset successfully");
        when(adminActionService.passwordReset(eq(1L), any(UserPasswordResetRequest.class)))
                .thenReturn(messageResponse);

        mockMvc.perform(patch("/api/v1/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordResetRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password reset successfully"));

        verify(adminActionService, times(1)).passwordReset(eq(1L), any(UserPasswordResetRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void passwordReset_NotFound_ReturnsNotFound() throws Exception {
        when(adminActionService.passwordReset(eq(1L), any(UserPasswordResetRequest.class)))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(patch("/api/v1/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordResetRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(adminActionService, times(1)).passwordReset(eq(1L), any(UserPasswordResetRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(adminActionService).delete(1L);

        mockMvc.perform(delete("/api/v1/admin/users/1"))
                .andExpect(status().isNoContent());

        verify(adminActionService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_NotFound_ReturnsNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User not found")).when(adminActionService).delete(1L);

        mockMvc.perform(delete("/api/v1/admin/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(adminActionService, times(1)).delete(1L);
    }
}
