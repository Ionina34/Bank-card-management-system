package banks.card.service.aspect;

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

@Aspect
@Component
@RequiredArgsConstructor
public class CheckingRightsCardAspect {

    private final CardUserActionService cardService;
    private final JwtService jwtService;

    @Before("@annotation(checkingRightsCard)")
    public void checkRights(JoinPoint joinPoint, CheckingRightsCard checkingRightsCard)
            throws EntityNotFoundException {
        Object[] args = joinPoint.getArgs();
        Long cardId = (Long) args[checkingRightsCard.cardIdIndex()];
        String token = (String) args[checkingRightsCard.tokenIdIndex()];

        String email = jwtService.extractEmail(token.substring(BEARER_PREFIX.length()));
        Card card = cardService.findById(cardId);

        if (!card.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("User " + email + " are not the owner of this card");
        }
    }
}
