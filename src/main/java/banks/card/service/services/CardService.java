package banks.card.service.services;

import banks.card.entity.Card;
import banks.card.exception.EntityNotFoundException;

/**
 * Интерфейс для каробы с банковскими картами.
 */
public interface CardService {

    /**
     * Находит карту по её идентификатору.
     *
     * @param id Идентификатор карты.
     * @return Объект {@link Card}, соответствующий указанному идентификатору.
     * @throws EntityNotFoundException если карта с указанным идентификатором не найдена.
     */
    Card findById(Long id);
}
