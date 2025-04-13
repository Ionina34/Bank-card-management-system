package banks.card.service.Impl.admin;

import banks.card.dto.in.card.CardInsertRequest;
import banks.card.dto.in.card.CardUpdateStatusRequest;
import banks.card.dto.in.card.UpdateCardLimitRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.CardUpsertResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.entity.Card;
import banks.card.entity.CardStatus;
import banks.card.entity.User;
import banks.card.exception.EntityNotFoundException;
import banks.card.repository.CardRepository;
import banks.card.service.services.amin.CardAdminActionService;
import banks.card.service.services.user.UserUserActionService;
import banks.card.service.mapper.CardMapper;
import banks.card.service.specification.CardSpecification;
import banks.card.utils.BeanUtils;
import banks.card.utils.CardMascEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardAdminServiceImpl implements CardAdminActionService {

    private final CardRepository cardRepository;
    private final UserUserActionService userService;
    private final CardMapper cardMapper;

    @Override
    public Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Card not found with ID: " + id)
                );
    }

    @Override
    public CardUpsertResponse create(String email, CardInsertRequest request)
            throws EntityNotFoundException, Exception {
        User user = userService.findByEmail(email);

        Card newCard = Card.builder()
                .encryptedCardNumber(CardMascEncryptor.encrypt(request.getCardNumber()))
                .cardHolder(request.getCardHolder())
                .expiryDate(request.getExpiryDate())
                .status(CardStatus.ACTIVE)
                .user(user)
                .build();
        cardRepository.save(newCard);

        return cardMapper.entityToUpsertResponse(newCard);
    }

    @Override
    public CardUpsertResponse updateStatus(CardUpdateStatusRequest request, Long cardId)
            throws EntityNotFoundException {
        Card card = findById(cardId);
        card.setStatus(request.getStatus());
        return cardMapper.entityToUpsertResponse(card);
    }

    @Override
    public void delete(Long id) {
        cardRepository.deleteById(id);
    }

    @Override
    public ListCardResponse getAllCards(CardFilterRequest filter, Pageable pageable) {
        Specification<Card> spec = CardSpecification.filterCards(filter);

        return cardMapper.listEntityToListResponse(
                cardRepository.findAll(spec, pageable)
        );
    }

    @Override
    @Transactional
    public CardResponse updateLimit(Long cardId, UpdateCardLimitRequest request) throws EntityNotFoundException {
        Card existsCard = findById(cardId);
        Card card = cardMapper.requestUpdateToEntity(request);

        BeanUtils.copyNotNullProperties(card, existsCard);
        return cardMapper.entityToResponse(
                cardRepository.save(existsCard)
        );
    }

}
