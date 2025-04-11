package banks.card.service.services.amin;

import banks.card.dto.in.card.CardInsertRequest;
import banks.card.dto.in.card.CardUpdateStatusRequest;
import banks.card.dto.in.card.UpdateCardLimitRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.CardUpsertResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.services.CardService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


public interface CardAdminActionService extends CardService {

    CardUpsertResponse create(String email, CardInsertRequest request)
            throws EntityNotFoundException, Exception;

    CardUpsertResponse updateStatus(CardUpdateStatusRequest request, Long cardId);

    void delete(Long id);

    ListCardResponse getAllCards(Pageable pageable);

    @Transactional
    CardResponse updateLimit(Long cardId, UpdateCardLimitRequest request) throws EntityNotFoundException;
}
