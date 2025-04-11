package banks.card.service.services.user;

import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.service.services.CardService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

public interface CardUserActionService extends CardService {

    ListCardResponse getCards(String token);

    @Transactional
    CardResponse blockedCard(Long cardId, String token) throws AccessDeniedException;
}
