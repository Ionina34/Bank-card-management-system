package banks.card.entity;

/**
 * Перечисление {@code TransferStatus} определяет возможные статусы банковской транзакции.
 * <p>
 * SUCCESS - Транзакция успешно завершена.
 * </p>
 * <p>
 * FAILED - Транзакция не удалась по техническим причинам.
 * </p>
 * <p>
 * DECLINED - Транзакция отклонена (например, из-за недостатка средств или ограничений).
 * </p>
 */
public enum TransferStatus {
    SUCCESS,
    FAILED,
    DECLINED
}
