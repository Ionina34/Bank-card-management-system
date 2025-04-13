package banks.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 64, nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_status", length = 64, nullable = false)
    private TransferStatus transferStatus;

    @Column(name = "transaction_date", nullable = false)
    private Timestamp transactionDate;

    @Column(name = "description", length = 20)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterpart_card_id")
    private Card counterpartCard;

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
