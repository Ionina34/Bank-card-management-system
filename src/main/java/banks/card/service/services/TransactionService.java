package banks.card.service.services;

import banks.card.dto.in.filter.TransactionFilterRequest;
import banks.card.dto.out.transaction.ListTransactionResponse;
import banks.card.entity.Card;
import banks.card.entity.Transaction;
import banks.card.entity.TransactionType;
import banks.card.entity.TransferStatus;
import banks.card.exception.EntityNotFoundException;
import banks.card.service.aspect.CheckingRightsCard;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Сервиса для работы с транзакциями пользователей.
 * Предоставляет методы для создания транзакций и получения списка транзакций.
 */
public interface TransactionService {

    /**
     * Создает и сохраняет транзакцию для перевода между картами.
     *
     * @param fromCard карта-источник
     * @param toCard карта-получатель
     * @param amount сумма транзакции
     * @param status статус транзакции
     * @param type тип транзакции
     * @param message сообщение о транзакции
     * @return объект {@link Transaction} сохраненной транзакции
     */
    Transaction createAndSave(Card fromCard, Card toCard, BigDecimal amount, TransferStatus status,
                              TransactionType type, String message);

    /**
     * Создает и сохраняет транзакцию для операций без карты-получателя (например, снятие).
     *
     * @param fromCard карта-источник
     * @param amount сумма транзакции
     * @param status статус транзакции
     * @param type тип транзакции
     * @param message сообщение о транзакции
     * @return объект {@link Transaction} сохраненной транзакции
     */
    Transaction createAndSave(Card fromCard, BigDecimal amount, TransferStatus status,
                              TransactionType type, String message);

    /**
     * Получает список транзакций пользователя по карте с учетом фильтрации и пагинации.
     *
     * @param cardId идентификатор карты
     * @param token токен авторизации
     * @param filter объект {@link TransactionFilterRequest} с параметрами фильтрации
     * @param pageable объект {@link Pageable} для настройки пагинации
     * @return объект {@link ListTransactionResponse} со списком транзакций
     * @throws EntityNotFoundException если карта не найдена
     */
    @CheckingRightsCard
    ListTransactionResponse getUserTransactions(Long cardId, String token, TransactionFilterRequest filter, Pageable pageable);

    /**
     * Получает список транзакций по карте с учетом фильтрации и пагинации.
     *
     * @param cardId идентификатор карты
     * @param filter объект {@link TransactionFilterRequest} с параметрами фильтрации
     * @param pageable объект {@link Pageable} для настройки пагинации
     * @return объект {@link ListTransactionResponse} со списком транзакций
     * @throws EntityNotFoundException если карта не найдена
     */
    ListTransactionResponse getCardTransactions(Long cardId, TransactionFilterRequest filter, Pageable pageable);

    /**
     * Находит транзакции по карте, выполненные после указанной даты и с указанными типами.
     *
     * @param card объект {@link Card}
     * @param date дата, после которой искать транзакции
     * @param types список типов транзакций
     * @return список объектов {@link Transaction}
     */
    List<Transaction> findByCardAndTransactionDateAfterAndTypeIn(Card card, Timestamp date, List<TransactionType> types);

    /**
     * Подсчитывает количество транзакций по карте, выполненных после указанной даты и с указанными типами.
     *
     * @param card объект {@link Card}
     * @param timestamp дата, после которой считать транзакции
     * @param types список типов транзакций
     * @return количество транзакций
     */
    long countByCardAndTransactionDateAfterAndTransactionTypeIn(Card card, Timestamp timestamp, List<TransactionType> types);
}
