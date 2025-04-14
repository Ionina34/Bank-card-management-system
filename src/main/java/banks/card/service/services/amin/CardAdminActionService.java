package banks.card.service.services.amin;

import banks.card.dto.in.card.CardInsertRequest;
import banks.card.dto.in.card.CardUpdateStatusRequest;
import banks.card.dto.in.card.UpdateCardLimitRequest;
import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.dto.out.card.CardResponse;
import banks.card.dto.out.card.CardUpsertResponse;
import banks.card.dto.out.card.ListCardResponse;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.services.CardService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Интерфейс расширяет {@link CardService}.
 * Сервиса для административных действий с банковскими картами.
 * Предоставляет методы для создания, обновления, удаления и фильтрации карт,
 * а также управления их статусом и лимитами.
 */
public interface CardAdminActionService extends CardService {

    /**
     * Создаёт новую карту для пользователя, указанного по email.
     *
     * @param email Электронная почта пользователя, для которого создаётся карта.
     * @param request Данные для создания карты {@link CardInsertRequest}.
     * @return Объект {@link CardUpsertResponse} с информацией о созданной карте.
     * @throws EntityNotFoundException если пользователь с указанным email не найден.
     * @throws Exception при ошибке шифрования или других проблемах.
     */
    CardUpsertResponse create(String email, CardInsertRequest request)
            throws EntityNotFoundException, Exception;

    /**
     * Обновляет статус карты по её идентификатору.
     *
     * @param request Данные для обновления статуса карты {@link CardUpdateStatusRequest}.
     * @param cardId Идентификатор карты.
     * @return Объект {@link CardUpsertResponse} с обновлённой информацией о карте.
     * @throws EntityNotFoundException если карта с указанным идентификатором не найдена.
     */
    CardUpsertResponse updateStatus(CardUpdateStatusRequest request, Long cardId);

    /**
     * Удаляет карту по её идентификатору.
     *
     * @param id Идентификатор карты.
     */
    void delete(Long id);

    /**
     * Получает список карт с применением фильтров и пагинации.
     *
     * @param filter Фильтр для выборки карт {@link CardFilterRequest}.
     * @param pageable Параметры пагинации.
     * @return Объект {@link ListCardResponse} со списком карт.
     */
    ListCardResponse getAllCards(CardFilterRequest filter, Pageable pageable);

    /**
     * Обновляет лимиты карты по её идентификатору.
     *
     * @param cardId Идентификатор карты.
     * @param request Данные для обновления лимитов карты {@link UpdateCardLimitRequest}.
     * @return Объект {@link CardResponse} с обновлённой информацией о карте.
     * @throws EntityNotFoundException если карта с указанным идентификатором не найдена.
     */
    @Transactional
    CardResponse updateLimit(Long cardId, UpdateCardLimitRequest request) throws EntityNotFoundException;
}
