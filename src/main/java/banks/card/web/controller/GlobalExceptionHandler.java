package banks.card.web.controller;

import banks.card.dto.out.card.TransferResponse;
import banks.card.dto.out.card.WithdrawalResponse;
import banks.card.dto.out.error.ErrorMessageResponse;
import banks.card.dto.out.error.ErrorTransferOrWithdrawalResponse;
import banks.card.exception.EntityNotFoundException;
import banks.card.exception.TransferException;
import banks.card.exception.WithdrawalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * Обрабатывает различные типы исключений, возникающих в приложении,
 * и возвращает соответствующие HTTP-ответы с сообщениями об ошибках.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения валидации входных данных.
     *
     * @param ex исключение {@link MethodArgumentNotValidException}, содержащее информацию об ошибках валидации
     * @return объект {@link ResponseEntity} с HTTP-статусом 400 (Bad Request) и строкой, содержащей описание ошибок валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Обрабатывает исключения, связанные с ненайденными сущностями.
     *
     * @param e исключение {@link EntityNotFoundException}, содержащее сообщение об ошибке
     * @return объект {@link ResponseEntity} с HTTP-статусом 404 (Not Found) и объектом {@link ErrorMessageResponse}, содержащим сообщение об ошибке
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(e.getMessage()));
    }

    /**
     * Обрабатывает исключения, связанные с отсутствием доступа.
     *
     * @param ex исключение {@link AccessDeniedException}, содержащее сообщение об ошибке
     * @return объект {@link ResponseEntity} с HTTP-статусом 403 (Forbidden) и объектом {@link ErrorMessageResponse}, содержащим сообщение об ошибке
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessageResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessageResponse(ex.getMessage()));
    }

    /**
     * Обрабатывает исключения, связанные с ошибками при выполнении переводов.
     *
     * @param ex исключение {@link TransferException}, содержащее сообщение об ошибке и объект ответа
     * @return объект {@link ResponseEntity} с HTTP-статусом 400 (Bad Request) и объектом {@link ErrorTransferOrWithdrawalResponse}, содержащим сообщение об ошибке и данные перевода
     */
    @ExceptionHandler(TransferException.class)
    public ResponseEntity<ErrorTransferOrWithdrawalResponse<TransferResponse>> handleTransferState(TransferException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorTransferOrWithdrawalResponse<>(ex.getMessage(), ex.getResponse()));
    }

    /**
     * Обрабатывает исключения, связанные с ошибками при снятии средств.
     *
     * @param ex исключение {@link WithdrawalException}, содержащее сообщение об ошибке и объект ответа
     * @return объект {@link ResponseEntity} с HTTP-статусом 400 (Bad Request) и объектом {@link ErrorTransferOrWithdrawalResponse}, содержащим сообщение об ошибке и данные снятия
     */
    @ExceptionHandler(WithdrawalException.class)
    public ResponseEntity<ErrorTransferOrWithdrawalResponse<WithdrawalResponse>> handleWithdrawalState(WithdrawalException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorTransferOrWithdrawalResponse<>(ex.getMessage(), ex.getResponse()));
    }
}