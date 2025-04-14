package banks.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Класс {@code Card} представляет сущность банковской карты в системе.
 * Содержит информацию о карте, такую как зашифрованный номер карты, владелец,
 * дата истечения срока действия, статус, баланс и лимиты транзакций.
 * Связана с пользователем ({@code User}) и списком транзакций ({@code Transaction}).
 */
@Entity
@Table(name = "cards")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {

    /**
     * Уникальный идентификатор карты.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Зашифрованный номер карты.
     * Не может быть пустым, максимальная длина — 256 символов.
     */
    @Column(name = "encrypted_card_number", length = 256, nullable = false)
    private String encryptedCardNumber;

    /**
     * Имя владельца карты.
     * Не может быть пустым, максимальная длина — 256 символов.
     */
    @Column(name = "card_holder", length = 256, nullable = false)
    private String cardHolder;

    /**
     * Дата истечения срока действия карты.
     * Не может быть пустой.
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    /**
     * Статус карты (например, активна, заблокирована).
     * Хранится как строка в базе данных.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private CardStatus status;

    /**
     * Текущий баланс карты.
     * По умолчанию равен нулю, точность — 15 цифр, 2 знака после запятой.
     */
    @Column(name = "balance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Дневной лимит транзакций.
     * Точность — 15 цифр, 2 знака после запятой.
     */
    @Column(name = "daily_limit", precision = 15, scale = 2)
    private BigDecimal dailyLimit;

    /**
     * Месячный лимит транзакций.
     * Точность — 15 цифр, 2 знака после запятой.
     */
    @Column(name = "monthly_limit", precision = 15, scale = 2)
    private BigDecimal monthlyLimit;

    /**
     * Лимит на одну транзакцию.
     * Точность — 15 цифр, 2 знака после запятой.
     */
    @Column(name = "single_transaction_limit", precision = 15, scale = 2)
    private BigDecimal singleTransactionLimit;

    /**
     * Максимальное количество транзакций в день.
     */
    @Column(name = "daily_transaction_count_limit", precision = 15, scale = 2)
    private Integer dailyTransactionCountLimit;

    /**
     * Пользователь, которому принадлежит карта.
     * Связь типа "многие к одному", ленивая загрузка.
     * Не может быть пустым.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Список транзакций, связанных с картой.
     * Связь типа "один ко многим", с каскадным удалением и обновлением.
     */
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}
