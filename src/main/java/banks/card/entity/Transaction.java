package banks.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Класс {@code Transaction} представляет сущность банковской транзакции в системе.
 * Содержит информацию о транзакции, такую как сумма, тип, статус, дата, описание,
 * а также связанные карты (основная и контрагент).
 */
@Entity
@Table(name = "transactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    /**
     * Уникальный идентификатор транзакции.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Карта, с которой осуществляется транзакция.
     * Связь типа "многие к одному", ленивая загрузка.
     * Не может быть пустой.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    /**
     * Сумма транзакции.
     * Точность — 15 цифр, 2 знака после запятой.
     * Не может быть пустой.
     */
    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Тип транзакции (например, перевод, пополнение, снятие).
     * Хранится как строка в базе данных.
     * Не может быть пустым.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 64, nullable = false)
    private TransactionType transactionType;

    /**
     * Статус перевода (например, успешно, отклонено).
     * Хранится как строка в базе данных.
     * Не может быть пустым.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_status", length = 64, nullable = false)
    private TransferStatus transferStatus;

    /**
     * Дата и время совершения транзакции.
     * Не может быть пустой.
     */
    @Column(name = "transaction_date", nullable = false)
    private Timestamp transactionDate;

    /**
     * Краткое описание транзакции.
     * Максимальная длина — 20 символов.
     */
    @Column(name = "description", length = 20)
    private String description;

    /**
     * Карта-контрагент, участвующая в транзакции (если применимо).
     * Связь типа "многие к одному", ленивая загрузка.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterpart_card_id")
    private Card counterpartCard;

    /**
     * Создаёт транзакцию с указанием карты отправителя, карты получателя, суммы,
     * статуса, типа и описания.
     *
     * @param fromCard карта отправителя
     * @param toCard   карта получателя
     * @param amount   сумма транзакции
     * @param status   статус перевода
     * @param type     тип транзакции
     * @param message  описание транзакции
     * @return объект {@code Transaction}
     */
    public static Transaction createTransaction(Card fromCard, Card toCard, BigDecimal amount, TransferStatus status,
                                                TransactionType type, String message) {
        return Transaction.builder()
                .card(fromCard)
                .amount(amount)
                .transferStatus(status)
                .transactionType(type)
                .transactionDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(message)
                .counterpartCard(toCard)
                .build();
    }

    /**
     * Создаёт транзакцию с указанием карты отправителя, суммы, статуса, типа и описания.
     * Используется, если карта получателя отсутствует (например, для пополнения или снятия).
     *
     * @param fromCard карта отправителя
     * @param amount   сумма транзакции
     * @param status   статус перевода
     * @param type     тип транзакции
     * @param message  описание транзакции
     * @return объект {@code Transaction}
     */
    public static Transaction createTransaction(Card fromCard, BigDecimal amount, TransferStatus status,
                                                TransactionType type, String message) {
        return Transaction.builder()
                .card(fromCard)
                .amount(amount)
                .transferStatus(status)
                .transactionType(type)
                .transactionDate(Timestamp.valueOf(LocalDateTime.now()))
                .description(message)
                .build();
    }
}
