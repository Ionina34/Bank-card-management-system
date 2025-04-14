package banks.card.web.controller.admin;

import banks.card.dto.in.user.UserCreateRequest;
import banks.card.dto.in.user.UserPasswordResetRequest;
import banks.card.dto.in.user.UserUpdateRequest;
import banks.card.dto.out.MessageResponse;
import banks.card.dto.out.error.ErrorMessageResponse;
import banks.card.dto.out.user.ListUserResponse;
import banks.card.dto.out.user.UserResponse;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.services.amin.UserAdminActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для администрирования пользователей.
 * Предоставляет методы для создания, получения, обновления, сброса пароля и удаления пользователей.
 */
@Tag(name = "Администрирование пользователей", description = "API для управления пользователями администратором")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminActionService userAdminService;

    /**
     * Создает нового пользователя.
     *
     * @param request объект с данными для создания пользователя
     * @return объект {@link UserResponse} с информацией о созданном пользователе
     */
    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя на основе предоставленных данных. Доступно только для пользователей с ролью ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Parameter(description = "Данные для создания пользователя") @RequestBody @Valid UserCreateRequest request) {
        UserResponse response = userAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Получает список всех пользователей с применением фильтра по роли и пагинации.
     *
     * @param role фильтр по роли пользователя (опционально)
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return объект {@link ListUserResponse} со списком пользователей
     */
    @Operation(
            summary = "Получить список пользователей",
            description = "Возвращает список пользователей с учетом фильтра по роли и пагинации. Доступно только для пользователей с ролью ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно возвращен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListUserResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListUserResponse> getAllUsers(
            @Parameter(description = "Фильтр по роли пользователя") @RequestParam(required = false, name = "role") String role,
            @Parameter(description = "Номер страницы", example = "0") @RequestParam(defaultValue = "0", name = "page") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10", name = "size") int size) {
        ListUserResponse response = userAdminService.getAll(PageRequest.of(page, size), role);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param request объект с данными для обновления пользователя
     * @param id      идентификатор пользователя
     * @return объект {@link UserResponse} с информацией об обновленном пользователе
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновляет данные пользователя по указанному идентификатору. Доступно только для пользователей с ролью ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанным идентификатором не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "Данные для обновления пользователя") @RequestBody @Valid UserUpdateRequest request,
            @Parameter(description = "Идентификатор пользователя", required = true) @PathVariable("id") Long id)
            throws EntityNotFoundException {
        UserResponse response = userAdminService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Сбрасывает пароль пользователя.
     *
     * @param request объект с данными для сброса пароля
     * @param id      идентификатор пользователя
     * @return объект {@link MessageResponse} с сообщением об успешном сбросе пароля
     * @throws EntityNotFoundException если пользователь с указанным идентификатором не найден
     */
    @Operation(
            summary = "Сбросить пароль пользователя",
            description = "Сбрасывает пароль пользователя по указанному идентификатору. Доступно только для пользователей с ролью ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль пользователя успешно сброшен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанным идентификатором не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN", content = @Content)
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> passwordReset(
            @Parameter(description = "Данные для сброса пароля") @RequestBody @Valid UserPasswordResetRequest request,
            @Parameter(description = "Идентификатор пользователя", required = true) @PathVariable("id") Long id)
            throws EntityNotFoundException {
        MessageResponse response = userAdminService.passwordReset(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return ответ с кодом 204 (No Content) при успешном удалении
     */
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по указанному идентификатору. Доступно только для пользователей с ролью ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанным идентификатором не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Идентификатор пользователя", required = true) @PathVariable("id") Long id) {
        userAdminService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
