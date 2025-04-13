package banks.card.web.controller.user;

import banks.card.dto.in.card.TransferRequest;
import banks.card.dto.in.card.WithdrawalRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.in.filter.TransactionFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.card.TransferResponse;
import banks.card.dto.out.card.WithdrawalResponse;
import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.entity.CardStatus;
import banks.card.entity.TransactionType;
import banks.card.entity.TransferStatus;
import banks.card.exception.EntityNotFoundException;
import banks.card.exception.TransferException;
import banks.card.service.services.user.CardUserActionService;
import banks.card.service.services.TransactionService;
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

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardUserActionService cardService;
    private final TransactionService transactionUserService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ListCardResponse> getAllCards(
            @RequestHeader(HEADER_NAME) String token,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(required = false, name = "number") String number,
            @RequestParam(required = false, name = "status") CardStatus status,
            @RequestParam(required = false, name = "min-balance") BigDecimal minBalance,
            @RequestParam(required = false, name = "max-balance") BigDecimal maxBalance)
            throws EntityNotFoundException {
        CardFilterRequest filter = new CardFilterRequest(number, status, minBalance, maxBalance);

        ListCardResponse response = cardService.getCards(token, filter, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ListTransactionResponse> getTransactions(
            @RequestHeader(HEADER_NAME) String token,
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(required = false, name = "type") TransactionType type,
            @RequestParam(required = false, name = "status") TransferStatus status,
            @RequestParam(required = false, name = "min-amount") BigDecimal minAmount,
            @RequestParam(required = false, name = "max-amount") BigDecimal maxAmount,
            @RequestParam(required = false, name = "date-from") LocalDateTime dateFrom,
            @RequestParam(required = false, name = "date-to") LocalDateTime dateTo)
            throws EntityNotFoundException, AccessDeniedException {
        TransactionFilterRequest filter =
                new TransactionFilterRequest(type, status, minAmount, maxAmount,
                        dateFrom == null ? null : Timestamp.valueOf(dateFrom),
                        dateTo == null ? null : Timestamp.valueOf(dateTo));

        ListTransactionResponse response = transactionUserService.getUserTransactions(id, token, filter, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/{id}/block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> blockedCard(
            @PathVariable("id") Long cardId,
            @RequestHeader(HEADER_NAME) String token)
            throws AccessDeniedException, EntityNotFoundException, IllegalStateException {
        CardResponse response = cardService.blockedCard(cardId, token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }


    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransferResponse> transfer(
            @RequestHeader(HEADER_NAME) String token,
            @RequestBody @Valid TransferRequest request)
            throws TransferException, EntityNotFoundException, AccessDeniedException {
        TransferResponse response = cardService.transfer(token, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/withdrawal")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WithdrawalResponse> withdrawal(
            @RequestHeader(HEADER_NAME) String token,
            @RequestBody @Valid WithdrawalRequest request)
            throws TransferException, EntityNotFoundException, AccessDeniedException {
        WithdrawalResponse response = cardService.withdraw(request, token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
