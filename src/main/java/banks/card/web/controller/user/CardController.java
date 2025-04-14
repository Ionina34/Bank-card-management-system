package banks.card.web.controller.user;

import banks.card.dto.in.card.TransferRequest;
import banks.card.dto.in.card.WithdrawalRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.in.filter.TransactionFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.card.TransferResponse;
import banks.card.dto.out.card.WithdrawalResponse;
import banks.card.dto.out.error.ErrorMessageResponse;
import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.entity.CardStatus;
import banks.card.entity.TransactionType;
import banks.card.entity.TransferStatus;
import banks.card.exception.EntityNotFoundException;
import banks.card.exception.TransferException;
import banks.card.service.services.user.CardUserActionService;
import banks.card.service.services.TransactionService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static banks.card.service.security.JwtService.HEADER_NAME;

/**
 * REST-контроллер для управления картами пользователя.
 * Предоставляет методы для получения списка карт, транзакций, блокировки карты,
 * выполнения переводов и снятия средств.
 */
@Tag(name = "Управление картами пользователя", description = "API для управления картами и транзакциями пользователя")
@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardUserActionService cardService;
    private final TransactionService transactionUserService;

    /**
     * Получает список карт пользователя с применением фильтров и пагинации.
     *
     * @param token      JWT-токен пользователя, переданный в заголовке
     * @param page       номер страницы (по умолчанию 0)
     * @param size       размер страницы (по умолчанию 10)
     * @param number     номер карты (опционально)
     * @param status     статус карты (опционально)
     * @param minBalance минимальный баланс (опционально)
     * @param maxBalance максимальный баланс (опционально)
     * @return объект {@link ListCardResponse} со списком карт
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Operation(
            summary = "Получить список карт пользователя",
            description = "Возвращает список карт пользователя с учетом фильтров и пагинации. Доступно только для пользователей с ролью USER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список карт успешно возвращен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListCardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль USER", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ListCardResponse> getAllCards(
            @Parameter(description = "JWT-токен пользователя", required = true) @RequestHeader(HEADER_NAME) String token,
            @Parameter(description = "Номер страницы", example = "0") @RequestParam(defaultValue = "0", name = "page") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10", name = "size") int size,
            @Parameter(description = "Номер карты") @RequestParam(required = false, name = "number") String number,
            @Parameter(description = "Статус карты") @RequestParam(required = false, name = "status") CardStatus status,
            @Parameter(description = "Минимальный баланс") @RequestParam(required = false, name = "min-balance") BigDecimal minBalance,
            @Parameter(description = "Максимальный баланс") @RequestParam(required = false, name = "max-balance") BigDecimal maxBalance)
            throws EntityNotFoundException {
        CardFilterRequest filter = new CardFilterRequest(number, status, minBalance, maxBalance);

        ListCardResponse response = cardService.getCards(token, filter, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Получает список транзакций для указанной карты пользователя с применением фильтров и пагинации.
     *
     * @param token     JWT-токен пользователя, переданный в заголовке
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
     * @throws EntityNotFoundException если карта или пользователь не найдены
     * @throws AccessDeniedException   если пользователь не имеет доступа к карте
     */
    @Operation(
            summary = "Получить транзакции карты",
            description = "Возвращает список транзакций для указанной карты пользователя с учетом фильтров и пагинации. Доступно только для пользователей с ролью USER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список транзакций успешно возвращен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListTransactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта или пользователь не найдены",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль USER или доступ к карте", content = @Content)
    })
    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ListTransactionResponse> getTransactions(
            @Parameter(description = "JWT-токен пользователя", required = true) @RequestHeader(HEADER_NAME) String token,
            @Parameter(description = "Идентификатор карты", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Номер страницы", example = "0") @RequestParam(defaultValue = "0", name = "page") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10", name = "size") int size,
            @Parameter(description = "Тип транзакции") @RequestParam(required = false, name = "type") TransactionType type,
            @Parameter(description = "Статус транзакции") @RequestParam(required = false, name = "status") TransferStatus status,
            @Parameter(description = "Минимальная сумма") @RequestParam(required = false, name = "min-amount") BigDecimal minAmount,
            @Parameter(description = "Максимальная сумма") @RequestParam(required = false, name = "max-amount") BigDecimal maxAmount,
            @Parameter(description = "Дата начала периода", example = "2023-01-01T00:00:00") @RequestParam(required = false, name = "date-from") LocalDateTime dateFrom,
            @Parameter(description = "Дата окончания периода", example = "2023-12-31T23:59:59") @RequestParam(required = false, name = "date-to") LocalDateTime dateTo)
            throws EntityNotFoundException, AccessDeniedException {
        TransactionFilterRequest filter =
                new TransactionFilterRequest(type, status, minAmount, maxAmount,
                        dateFrom == null ? null : Timestamp.valueOf(dateFrom),
                        dateTo == null ? null : Timestamp.valueOf(dateTo));

        ListTransactionResponse response = transactionUserService.getUserTransactions(id, token, filter, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Блокирует карту пользователя.
     *
     * @param cardId идентификатор карты
     * @param token  JWT-токен пользователя, переданный в заголовке
     * @return объект {@link CardResponse} с информацией о заблокированной карте
     * @throws AccessDeniedException   если пользователь не имеет доступа к карте
     * @throws EntityNotFoundException если карта не найдена
     * @throws IllegalStateException   если карта уже заблокирована
     */
    @Operation(
            summary = "Заблокировать карту",
            description = "Блокирует указанную карту пользователя. Доступно только для пользователей с ролью USER, если карта принадлежит им.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль USER или доступ к карте", content = @Content),
            @ApiResponse(responseCode = "409", description = "Карта уже заблокирована",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
    })
    @PostMapping("/{id}/block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> blockedCard(
            @Parameter(description = "Идентификатор карты", required = true) @PathVariable("id") Long cardId,
            @Parameter(description = "JWT-токен пользователя", required = true) @RequestHeader(HEADER_NAME) String token)
            throws AccessDeniedException, EntityNotFoundException, IllegalStateException {
        CardResponse response = cardService.blockedCard(cardId, token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Выполняет перевод средств между картами.
     *
     * @param token   JWT-токен пользователя, переданный в заголовке
     * @param request объект с данными для перевода
     * @return объект {@link TransferResponse} с информацией о переводе
     * @throws TransferException       если перевод не удался
     * @throws EntityNotFoundException если карта или пользователь не найдены
     * @throws AccessDeniedException   если пользователь не имеет доступа к карте
     */
    @Operation(
            summary = "Выполнить перевод",
            description = "Выполняет перевод средств между картами. Доступно только для пользователей с ролью USER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransferResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса или ошибка перевода",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта или пользователь не найдены",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль USER или доступ к карте", content = @Content)
    })
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransferResponse> transfer(
            @Parameter(description = "JWT-токен пользователя", required = true) @RequestHeader(HEADER_NAME) String token,
            @Parameter(description = "Данные для перевода") @RequestBody @Valid TransferRequest request)
            throws TransferException, EntityNotFoundException, AccessDeniedException {
        TransferResponse response = cardService.transfer(token, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Выполняет снятие средств с карты.
     *
     * @param token   JWT-токен пользователя, переданный в заголовке
     * @param request объект с данными для снятия средств
     * @return объект {@link WithdrawalResponse} с информацией о снятии
     * @throws TransferException       если снятие не удалось
     * @throws EntityNotFoundException если карта или пользователь не найдены
     * @throws AccessDeniedException   если пользователь не имеет доступа к карте
     */
    @Operation(
            summary = "Снять средства с карты",
            description = "Выполняет снятие средств с карты пользователя. Доступно только для пользователей с ролью USER."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Снятие средств успешно выполнено",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WithdrawalResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса или ошибка снятия",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта или пользователь не найдены",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: требуется роль USER или доступ к карте", content = @Content)
    })
    @PostMapping("/withdrawal")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WithdrawalResponse> withdrawal(
            @Parameter(description = "JWT-токен пользователя", required = true) @RequestHeader(HEADER_NAME) String token,
            @Parameter(description = "Данные для снятия средств") @RequestBody @Valid WithdrawalRequest request)
            throws TransferException, EntityNotFoundException, AccessDeniedException {
        WithdrawalResponse response = cardService.withdraw(request, token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}