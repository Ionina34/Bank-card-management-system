package banks.card.service.aspect;

import banks.card.dto.in.card.TransferRequest;
import banks.card.dto.out.card.WithdrawalResponse;
import banks.card.entity.Card;
import banks.card.exception.EntityNotFoundException;

import banks.card.service.security.JwtService;
import banks.card.service.services.user.CardUserActionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import static banks.card.service.security.JwtService.BEARER_PREFIX;

/**
 * Аспект {@code CheckingRightsCardAspect} реализует проверку прав доступа пользователя к картам
 * перед выполнением операций. Используется для методов, аннотированных {@code CheckingRightsCard}
 * или {@code CheckingRightsCards}, чтобы убедиться, что пользователь является владельцем карты(т).
 */
@Aspect
@Component
@RequiredArgsConstructor
public class CheckingRightsCardAspect {

    /**
     * Сервис для выполнения операций с картами пользователя.
     */
    private final CardUserActionService cardService;

    /**
     * Сервис для работы с JWT-токенами.
     */
    private final JwtService jwtService;

    /**
     * Проверяет права доступа пользователя к карте перед выполнением метода, аннотированного
     * {@code CheckingRightsCard}. Извлекает email из токена и сравнивает его с email владельца карты.
     *
     * @param joinPoint точка соединения, предоставляющая доступ к аргументам метода
     * @param checkingRightsCard аннотация с информацией об индексах параметров токена и карты
     * @throws EntityNotFoundException если карта не найдена
     * @throws AccessDeniedException если пользователь не является владельцем карты
     */
    @Before("@annotation(checkingRightsCard)")
    public void checkRights4Card(JoinPoint joinPoint, CheckingRightsCard checkingRightsCard)
            throws EntityNotFoundException {
        Object[] args = joinPoint.getArgs();
        String token = (String) args[checkingRightsCard.tokenIdIndex()];
        Object cardId = (Object) args[checkingRightsCard.cardIdIndex()];

        Card card = null;
        if(cardId instanceof Long){
            card = cardService.findById((Long) cardId);
        } else if(cardId instanceof WithdrawalResponse){
            card = cardService.findById(((WithdrawalResponse) cardId).getCardId());
        }

        String email = jwtService.extractEmail(token.substring(BEARER_PREFIX.length()));

        if (!card.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("User " + email + " are not the owner of this card");
        }
    }

    /**
     * Проверяет права доступа пользователя к обеим картам (отправителя и получателя) перед выполнением
     * метода, аннотированного {@code CheckingRightsCards}. Убедится, что пользователь является владельцем
     * обеих карт, указанных в запросе на перевод.
     *
     * @param joinPoint точка соединения, предоставляющая доступ к аргументам метода
     * @param checkingRightsCards аннотация с информацией об индексах параметров токена и запроса
     * @throws EntityNotFoundException если одна из карт не найдена
     * @throws AccessDeniedException если пользователь не является владельцем одной из карт
     */
    @Before("@annotation(checkingRightsCards)")
    public void checkRights4Cards(JoinPoint joinPoint, CheckingRightsCards checkingRightsCards)
            throws EntityNotFoundException {
        Object[] args = joinPoint.getArgs();
        TransferRequest request = (TransferRequest) args[checkingRightsCards.requestIdIndex()];
        String token = (String) args[checkingRightsCards.tokenIdIndex()];

        String email = jwtService.extractEmail(token.substring(BEARER_PREFIX.length()));
        Card fromCard = cardService.findById(request.getFromCardId());
        Card toCard = cardService.findById(request.getToCardId());

        if (!fromCard.getUser().getEmail().equals(email) || !toCard.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("User " + email + " are not the owner one of the card");
        }
    }
}
