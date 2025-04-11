package banks.card.web.controller.user;

import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.services.user.CardUserActionService;
import banks.card.service.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static banks.card.service.security.JwtService.HEADER_NAME;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardUserActionService cardService;
    private TransactionService transactionUserService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ListCardResponse> getAllCards(
            @RequestHeader(HEADER_NAME) String token,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size)
            throws EntityNotFoundException {
        ListCardResponse response = cardService.getCards(token);
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

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ListTransactionResponse> getTransactions(
            @RequestHeader(HEADER_NAME) String token,
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size)
            throws EntityNotFoundException, AccessDeniedException {
        ListTransactionResponse response = transactionUserService.getUserTransactions(id, token, PageRequest.of(page,size));
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
