package banks.card.web.controller.admin;

import banks.card.dto.in.card.CardInsertRequest;
import banks.card.dto.in.card.CardUpdateStatusRequest;
import banks.card.dto.in.card.UpdateCardLimitRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.CardUpsertResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.transaction.ListTransactionResponse;
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

@RestController
@RequestMapping("/api/v1/admin/cards")
@RequiredArgsConstructor
public class CardAdminController {

    private final CardAdminActionService cardService;
    private final TransactionService transactionService;

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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListCardResponse> getAll(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size) {
        ListCardResponse response = cardService.getAllCards(PageRequest.of(page, size));
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

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListTransactionResponse> getCardTransactions(
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size)
            throws EntityNotFoundException {
        ListTransactionResponse response = transactionService.getCardTransactions(id, PageRequest.of(page, size));
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
