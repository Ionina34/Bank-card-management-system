package banks.card.entity;

/**
 * Перечисление {@code TransactionType} определяет возможные типы банковских транзакций.
 * <p>
 *     WITHDRAWAL - Снятие средств с карты.
 * </p>
 * <p>
 *     DEPOSIT - Пополнение карты.
 * </p>
 * <p>
 *     TRANSFER_OUT - Перевод средств с карты (исходящий перевод).
 * </p>
 * <p>
 *     TRANSFER_IN - Получение средств на карту (входящий перевод).
 * </p>
 */
public enum TransactionType {
    WITHDRAWAL,
    DEPOSIT,
    TRANSFER_OUT,
    TRANSFER_IN
}
