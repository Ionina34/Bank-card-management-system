package banks.card.web.controller.admin;

import banks.card.dto.in.user.UserCreateRequest;
import banks.card.dto.in.user.UserPasswordResetRequest;
import banks.card.dto.in.user.UserUpdateRequest;
import banks.card.dto.out.MessageResponse;
import banks.card.dto.out.user.ListUserResponse;
import banks.card.dto.out.user.UserResponse;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.services.amin.UserAdminActionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminActionService userAdminService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
        UserResponse response = userAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListUserResponse> getAllUsers(
            @RequestParam(required = false, name = "role") String role,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size) {
        ListUserResponse response = userAdminService.getAll(PageRequest.of(page, size), role);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @RequestBody @Valid UserUpdateRequest request,
            @PathVariable("id") Long id)
            throws EntityNotFoundException {
        UserResponse response = userAdminService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> passwordReset(
            @RequestBody @Valid UserPasswordResetRequest request,
            @PathVariable("id") Long id) throws EntityNotFoundException {
        MessageResponse response = userAdminService.passwordReset(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){
        userAdminService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
