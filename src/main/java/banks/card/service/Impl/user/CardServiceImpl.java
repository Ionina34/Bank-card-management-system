package banks.card.service.Impl.user;

import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.entity.Card;
import banks.card.entity.CardStatus;
import banks.card.entity.User;
import banks.card.exception.EntityNotFoundException;
import banks.card.repository.CardRepository;
import banks.card.service.aspect.CheckingRightsCard;
import banks.card.service.services.user.CardUserActionService;
import banks.card.service.services.user.UserUserActionService;
import banks.card.service.mapper.CardMapper;
import banks.card.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static banks.card.service.security.JwtService.BEARER_PREFIX;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardUserActionService {

    private final CardRepository cardRepository;
    private final UserUserActionService userService;
    private final JwtService jwtService;
    private final CardMapper cardMapper;

    @Override
    public Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Card not found by ID: " + id));
    }

    @Override
    public ListCardResponse getCards(String token) throws EntityNotFoundException {
        token = token.substring(BEARER_PREFIX.length());
        String email = jwtService.extractEmail(token);
        User user = userService.findByEmail(email);
        return cardMapper.listEntityToListResponse(
                cardRepository.findByUser(user)
        );
    }

    @Override
    @CheckingRightsCard(cardIdIndex = 0, tokenIdIndex = 1)
    @Transactional
    public CardResponse blockedCard(Long cardId, String token)
            throws EntityNotFoundException, IllegalStateException {
        Card card = findById(cardId);

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalStateException("Cannot block an expired card");
        }
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new IllegalStateException("Cannot block an expired card");
        }

        card.setStatus(CardStatus.BLOCKED);
        Card updateCard = cardRepository.save(card);

        return cardMapper.entityToResponse(updateCard);
    }
}
