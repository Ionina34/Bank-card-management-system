package banks.card.service.services.user;

import banks.card.dto.in.card.TransferRequest;
import banks.card.dto.in.card.WithdrawalRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.card.TransferResponse;
import banks.card.dto.out.card.WithdrawalResponse;
import banks.card.exception.EntityNotFoundException;
import banks.card.exception.TransferException;
import banks.card.exception.WithdrawalException;
import banks.card.service.aspect.CheckingRightsCard;
import banks.card.service.aspect.CheckingRightsCards;
import banks.card.service.services.CardService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

public interface CardUserActionService extends CardService {

    ListCardResponse getCards(String token);

    @CheckingRightsCard
    @Transactional
    CardResponse blockedCard(Long cardId, String token) throws AccessDeniedException;

    @CheckingRightsCards
    @Transactional
    TransferResponse transfer(String token, TransferRequest request) throws AccessDeniedException, EntityNotFoundException, TransferException;

    @CheckingRightsCard
    @Transactional
    WithdrawalResponse withdraw(WithdrawalRequest request, String token) throws AccessDeniedException, EntityNotFoundException, WithdrawalException;
}
