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
import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.entity.CardStatus;
import banks.card.entity.TransactionType;
import banks.card.entity.TransferStatus;
import banks.card.service.security.JwtService;
import banks.card.service.services.TransactionService;
import banks.card.service.services.amin.CardAdminActionService;
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

@RestController
@RequestMapping("/api/v1/admin/cards")
@RequiredArgsConstructor
public class CardAdminController {

    private final CardAdminActionService cardService;
    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListCardResponse> getAll(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(required = false, name = "number") String number,
            @RequestParam(required = false, name = "status") CardStatus status,
            @RequestParam(required = false, name = "min-balance") BigDecimal minBalance,
            @RequestParam(required = false, name = "max-balance") BigDecimal maxBalance) {
        CardFilterRequest filter = new CardFilterRequest(number, status, minBalance, maxBalance);

        ListCardResponse response = cardService.getAllCards(filter, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListTransactionResponse> getCardTransactions(
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(required = false, name = "type") TransactionType type,
            @RequestParam(required = false, name = "status") TransferStatus status,
            @RequestParam(required = false, name = "min-amount") BigDecimal minAmount,
            @RequestParam(required = false, name = "max-amount") BigDecimal maxAmount,
            @RequestParam(required = false, name = "date-from") LocalDateTime dateFrom,
            @RequestParam(required = false, name = "date-to") LocalDateTime dateTo)
            throws EntityNotFoundException {
        TransactionFilterRequest filter =
                new TransactionFilterRequest(type, status, minAmount, maxAmount,
                        Timestamp.valueOf(dateFrom), Timestamp.valueOf(dateTo));

        ListTransactionResponse response = transactionService.getCardTransactions(id,filter, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/user/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardUpsertResponse> addCard(
            @PathVariable("email") String email,
            @RequestBody @Valid CardInsertRequest request) throws EntityNotFoundException, Exception {
        CardUpsertResponse response = cardService.create(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardUpsertResponse> updateStatus(
            @PathVariable("id") Long id,
            @RequestBody CardUpdateStatusRequest request)
            throws EntityNotFoundException {
        CardUpsertResponse response = cardService.updateStatus(request, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/{id}/limits")
    @PreAuthorize("hasHole('ADMIN')")
    public ResponseEntity<CardResponse> updateLimits(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateCardLimitRequest request) throws EntityNotFoundException {
        CardResponse response = cardService.updateLimit(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
