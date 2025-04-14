package banks.card.service.services.user;

import banks.card.dto.in.card.TransferRequest;
import banks.card.dto.in.card.WithdrawalRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.dto.out.card.TransferResponse;
import banks.card.dto.out.card.WithdrawalResponse;
import banks.card.entity.Card;
import banks.card.exception.EntityNotFoundException;
import banks.card.exception.TransferException;
import banks.card.exception.WithdrawalException;
import banks.card.service.aspect.CheckingRightsCard;
import banks.card.service.aspect.CheckingRightsCards;
import banks.card.service.services.CardService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Интерфейс расширяет {@link CardService}
 * Сервиса для действий пользователя с картами.
 * Предоставляет методы для получения информации о картах, их блокировки,
 * выполнения переводов и снятия средств.
 */
public interface CardUserActionService extends CardService {

    /**
     * Получает список карт пользователя с учетом фильтрации и пагинации.
     *
     * @param token    токен авторизации
     * @param filter   объект {@link CardFilterRequest} с параметрами фильтрации
     * @param pageable объект {@link Pageable} для настройки пагинации
     * @return объект {@link ListCardResponse} со списком карт
     * @throws EntityNotFoundException если пользователь не найден
     */
    ListCardResponse getCards(String token, CardFilterRequest filter, Pageable pageable);

    /**
     * Блокирует карту по её идентификатору.
     *
     * @param cardId идентификатор карты
     * @param token  токен авторизации
     * @return объект {@link CardResponse} с данными обновленной карты
     * @throws EntityNotFoundException если карта не найдена
     * @throws IllegalStateException   если карта уже заблокирована или истек срок действия
     */
    @CheckingRightsCard
    @Transactional
    CardResponse blockedCard(Long cardId, String token) throws AccessDeniedException;

    /**
     * Выполняет перевод между картами.
     *
     * @param token   токен авторизации
     * @param request объект {@link TransferRequest} с данными для перевода
     * @return объект {@link TransferResponse} с результатом перевода
     * @throws AccessDeniedException   если у пользователя нет прав на операцию
     * @throws EntityNotFoundException если карта не найдена
     * @throws TransferException       если перевод не выполнен по бизнес-причинам
     */
    @CheckingRightsCards
    @Transactional
    TransferResponse transfer(String token, TransferRequest request) throws AccessDeniedException, EntityNotFoundException, TransferException;


    /**
     * Выполняет снятие средств с карты.
     *
     * @param request объект {@link WithdrawalRequest} с данными для снятия
     * @param token   токен авторизации
     * @return объект {@link WithdrawalResponse} с результатом снятия
     * @throws AccessDeniedException   если у пользователя нет прав на операцию
     * @throws EntityNotFoundException если карта не найдена
     * @throws WithdrawalException     если снятие не выполнено по бизнес-причинам
     */
    @CheckingRightsCard
    @Transactional
    WithdrawalResponse withdraw(WithdrawalRequest request, String token) throws AccessDeniedException, EntityNotFoundException, WithdrawalException;
}
