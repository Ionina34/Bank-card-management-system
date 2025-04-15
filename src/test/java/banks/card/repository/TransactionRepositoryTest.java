package banks.card.repository;

import banks.card.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    private Card card;
    private User user;
    private Timestamp dateT1;
    private Timestamp dateT2;
    private TransactionType type;
    private TransferStatus status;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .email("test@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();
        user = userRepository.save(user);

        card = Card.builder()
                .encryptedCardNumber("1234-5678-9012-3456")
                .user(user)
                .cardHolder("IVAN IVANOV")
                .expiryDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .build();
        card = cardRepository.save(card);

        dateT1 = Timestamp.valueOf("2023-01-01 16:59:59");
        dateT2 = Timestamp.valueOf("2023-01-01 23:59:59");
        type = TransactionType.TRANSFER_OUT;
        status = TransferStatus.SUCCESS;

        Transaction t1 = Transaction.builder()
                .card(card)
                .description("")
                .transactionDate(dateT1)
                .transactionType(type)
                .transferStatus(status)
                .amount(BigDecimal.TEN)
                .build();
        Transaction t2 = Transaction.builder()
                .card(card)
                .description("")
                .transactionDate(dateT2)
                .transactionType(type)
                .transferStatus(status)
                .amount(BigDecimal.valueOf(200.0))
                .build();
        transactionRepository.save(t1);
        transactionRepository.save(t2);
    }

    @Test
    void findByCard_WithSpecificationAndPagination_ReturnsCorrectPage() {
        Pageable pageable = PageRequest.of(0, 1);
        Specification<Transaction> spec = (root, query, cb) -> cb.greaterThan(root.get("amount"), 150.0);

        Page<Transaction> result = transactionRepository.findByCard(card, spec, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAmount()).isEqualTo(BigDecimal.valueOf(200.0));
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findByCardAndTransactionDateAfterAndTransactionTypeIn_ReturnsFilteredTransactions() {
       Timestamp dateAfter = Timestamp.valueOf("2023-01-01 00:00:00");

        List<TransactionType> types = List.of(type);

        List<Transaction> result = transactionRepository.findByCardAndTransactionDateAfterAndTransactionTypeIn(
                card, dateAfter, types);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(result.get(1).getAmount()).isEqualTo(BigDecimal.valueOf(200.0));
    }

    @Test
    void countByCardAndTransactionDateAfterAndTransactionTypeIn_ReturnsCorrectCount() {
        List<TransactionType> types = List.of(type);
        Timestamp dateAfter = Timestamp.valueOf("2023-01-01 18:00:00");

        long count = transactionRepository.countByCardAndTransactionDateAfterAndTransactionTypeIn(
                card, dateAfter, types);

        assertThat(count).isEqualTo(1);
    }
}
