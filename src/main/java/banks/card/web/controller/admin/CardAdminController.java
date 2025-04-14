package banks.card.web.controller.admin;

import banks.card.dto.in.card.CardInsertRequest;
import banks.card.dto.in.card.CardUpdateStatusRequest;
import banks.card.dto.in.card.TransferRequest;
import banks.card.dto.in.card.UpdateCardLimitRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.in.filter.TransactionFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.CardUpsertResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.card.TransferResponse;
import banks.card.dto.out.error.ErrorMessageResponse;
import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.entity.CardStatus;
import banks.card.entity.TransactionType;
import banks.card.entity.TransferStatus;
import banks.card.service.security.JwtService;
import banks.card.service.services.TransactionService;
import banks.card.service.services.amin.CardAdminActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static banks.card.service.security.JwtService.HEADER_NAME;

/**
 * REST-контроллер для администрирования карт.
 * Предоставляет методы для получения, создания, обновления и удаления карт,
 * а также получения транзакций по картам.
 */
@Tag(name = "Администрирование карт", description = "API для управления картами администратором")
@RestController
@RequestMapping("/api/v1/admin/cards")
@RequiredArgsConstructor
public class CardAdminController {

    private final CardAdminActionService cardService;
    private final TransactionService transactionService;

    /**
     * Получает список всех карт с применением фильтров и пагинации.
     *
     * @param page       номер страницы (по умолчанию 0)
     * @param size       размер страницы (по умолчанию 10)
     * @param number     номер карты (опционально)
     * @param status     статус карты (опционально)
     * @param minBalance минимальный баланс (опционально)
     * @param maxBalance максимальный баланс (опционально)
     * @return объект {@link ListCardResponse} с отфильтрованным списком карт
     */
    @Operation(
            summary = "Получить список карт",
            description = "Возвращает список карт с учетом фильтров и пагинации. Доступно только для пользователей с ролью ADMIN.",
            tags = {"Администрирование карт"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список карт успешно возвращен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListCardResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN",
                    content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListCardResponse> getAll(
            @Parameter(description = "Номер страницы", example = "0") @RequestParam(defaultValue = "0", name = "page") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10", name = "size") int size,
            @Parameter(description = "Номер карты") @RequestParam(required = false, name = "number") String number,
            @Parameter(description = "Статус карты") @RequestParam(required = false, name = "status") CardStatus status,
            @Parameter(description = "Минимальный баланс") @RequestParam(required = false, name = "min-balance") BigDecimal minBalance,
            @Parameter(description = "Максимальный баланс") @RequestParam(required = false, name = "max-balance") BigDecimal maxBalance) {
        CardFilterRequest filter = new CardFilterRequest(number, status, minBalance, maxBalance);

        ListCardResponse response = cardService.getAllCards(filter, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Получает список транзакций для указанной карты с применением фильтров и пагинации.
     *
     * @param id        идентификатор карты
     * @param page      номер страницы (по умолчанию 0)
     * @param size      размер страницы (по умолчанию 10)
     * @param type      тип транзакции (опционально)
     * @param status    статус транзакции (опционально)
     * @param minAmount минимальная сумма транзакции (опционально)
     * @param maxAmount максимальная сумма транзакции (опционально)
     * @param dateFrom  дата начала периода (опционально)
     * @param dateTo    дата окончания периода (опционально)
     * @return объект {@link ListTransactionResponse} со списком транзакций
     * @throws EntityNotFoundException если карта с указанным идентификатором не найдена
     */
    @Operation(
            summary = "Получить транзакции карты",
            description = "Возвращает список транзакций для указанной карты с учетом фильтров и пагинации. Доступно только для пользователей с ролью ADMIN.",
            tags = {"Администрирование карт"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список транзакций успешно возвращен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListTransactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта с указанным идентификатором не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN", content = @Content)
    })
    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListTransactionResponse> getCardTransactions(
            @Parameter(description = "Идентификатор карты", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Номер страницы", example = "0") @RequestParam(defaultValue = "0", name = "page") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10", name = "size") int size,
            @Parameter(description = "Тип транзакции") @RequestParam(required = false, name = "type") TransactionType type,
            @Parameter(description = "Статус транзакции") @RequestParam(required = false, name = "status") TransferStatus status,
            @Parameter(description = "Минимальная сумма") @RequestParam(required = false, name = "min-amount") BigDecimal minAmount,
            @Parameter(description = "Максимальная сумма") @RequestParam(required = false, name = "max-amount") BigDecimal maxAmount,
            @Parameter(description = "Дата начала периода") @RequestParam(required = false, name = "date-from") LocalDateTime dateFrom,
            @Parameter(description = "Дата окончания периода") @RequestParam(required = false, name = "date-to") LocalDateTime dateTo)
            throws EntityNotFoundException {
        TransactionFilterRequest filter =
                new TransactionFilterRequest(type, status, minAmount, maxAmount,
                        dateFrom == null ? null : Timestamp.valueOf(dateFrom),
                        dateTo == null ? null : Timestamp.valueOf(dateTo));

        ListTransactionResponse response = transactionService.getCardTransactions(id, filter, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Создает новую карту для пользователя по его email.
     *
     * @param email   email пользователя
     * @param request объект с данными для создания карты
     * @return объект {@link CardUpsertResponse} с информацией о созданной карте
     * @throws EntityNotFoundException если пользователь с указанным email не найден
     * @throws Exception               при ошибке создания карты
     */
    @Operation(
            summary = "Создать новую карту",
            description = "Создает новую карту для пользователя по его email. Доступно только для пользователей с ролью ADMIN.",
            tags = {"Администрирование карт"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Карта успешно создана", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CardUpsertResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанным email не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
    })
    @PostMapping("/user/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardUpsertResponse> addCard(
            @Parameter(description = "Email пользователя", required = true) @PathVariable("email") String email,
            @Parameter(description = "Данные для создания карты") @RequestBody @Valid CardInsertRequest request) throws EntityNotFoundException, Exception {
        CardUpsertResponse response = cardService.create(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Обновляет статус карты.
     *
     * @param id      идентификатор карты
     * @param request объект с новым статусом карты
     * @return объект {@link CardUpsertResponse} с информацией об обновленной карте
     * @throws EntityNotFoundException если карта с указанным идентификатором не найдена
     */
    @Operation(
            summary = "Обновить статус карты",
            description = "Обновляет статус указанной карты. Доступно только для пользователей с ролью ADMIN.",
            tags = {"Администрирование карт"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус карты успешно обновлен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardUpsertResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта с указанным идентификатором не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardUpsertResponse> updateStatus(
            @Parameter(description = "Идентификатор карты", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Новый статус карты") @RequestBody CardUpdateStatusRequest request)
            throws EntityNotFoundException {
        CardUpsertResponse response = cardService.updateStatus(request, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Обновляет лимиты карты.
     *
     * @param id      идентификатор карты
     * @param request объект с новыми лимитами карты
     * @return объект {@link CardResponse} с информацией об обновленной карте
     * @throws EntityNotFoundException если карта с указанным идентификатором не найдена
     */
    @Operation(
            summary = "Обновить лимиты карты",
            description = "Обновляет лимиты для карты с указанным идентификатором. Доступно только для(users with role ADMIN.",
            tags = {"Администрирование карт"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Лимиты карты успешно обновлены",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта с указанным идентификатором не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN", content = @Content)
    })
    @PatchMapping("/{id}/limits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> updateLimits(
            @Parameter(description = "Идентификатор карты", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Объект с новыми лимитами карты") @RequestBody @Valid UpdateCardLimitRequest request)
            throws EntityNotFoundException {
        CardResponse response = cardService.updateLimit(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Удаляет карту по её идентификатору.
     *
     * @param id идентификатор карты
     * @return ответ с кодом 204 (No Content) при успешном удалении
     */
    @Operation(
            summary = "Удалить карту",
            description = "Удаляет карту по указанному идентификатору. Доступно только для пользователей с ролью ADMIN.",
            tags = {"Администрирование карт"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Карта успешно удалена", content = @Content),
            @ApiResponse(responseCode = "404", description = "Карта с указанным идентификатором не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль ADMIN", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор карты", required = true) @PathVariable("id") Long id) {
        cardService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
