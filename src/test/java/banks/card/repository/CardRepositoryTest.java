package banks.card.repository;

import banks.card.entity.Card;
import banks.card.entity.CardStatus;
import banks.card.entity.Role;
import banks.card.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardRepositoryTest extends AbstractRepositoryTest{

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .email("test@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();
        user = userRepository.save(user);
    }

    @Test
    void findByUser_WithNoCards_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Card> spec = null;

        Page<Card> result = cardRepository.findByUser(user, spec, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void findByUser_WithCards_ReturnsCorrectPage() {
        Card card1 = Card.builder()
                .encryptedCardNumber("1234-5678-9012-3456")
                .user(user)
                .cardHolder("IVAN IVANOV")
                .expiryDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .build();
        Card card2 = Card.builder()
                .encryptedCardNumber("9876-5432-1098-7654")
                .user(user)
                .cardHolder("IVAN IVANOV")
                .expiryDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .build();
        cardRepository.saveAll(List.of(card1, card2));

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Card> spec = null;

        Page<Card> result = cardRepository.findByUser(user, spec, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().anyMatch(card -> card.getEncryptedCardNumber().equals("1234-5678-9012-3456")));
        assertTrue(result.getContent().stream().anyMatch(card -> card.getEncryptedCardNumber().equals("9876-5432-1098-7654")));
    }

    @Test
    void findByUser_WithSpecification_ReturnsFilteredCards() {
        Card card1 = Card.builder()
                .encryptedCardNumber("1234-5678-9012-3456")
                .user(user)
                .cardHolder("IVAN IVANOV")
                .expiryDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .build();
        Card card2 = Card.builder()
                .encryptedCardNumber("9876-5432-1098-7654")
                .user(user)
                .cardHolder("IVAN IVANOV")
                .expiryDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .build();
        cardRepository.saveAll(List.of(card1, card2));

        Specification<Card> spec = (root, query, cb) -> cb.equal(root.get("encryptedCardNumber"), "1234-5678-9012-3456");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Card> result = cardRepository.findByUser(user, spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("1234-5678-9012-3456", result.getContent().get(0).getEncryptedCardNumber());
    }

    @Test
    void findByUser_WithPagination_ReturnsCorrectPageSize() {
        Card card1 = Card.builder()
                .encryptedCardNumber("1234-5678-9012-3456")
                .user(user)
                .cardHolder("IVAN IVANOV")
                .expiryDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .build();
        Card card2 = Card.builder()
                .encryptedCardNumber("9876-5432-1098-7654")
                .user(user)
                .cardHolder("IVAN IVANOV")
                .expiryDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .build();
        Card card3 = Card.builder()
                .encryptedCardNumber("1111-2222-3333-4444")
                .user(user)
                .cardHolder("IVAN IVANOV")
                .expiryDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .build();
        cardRepository.saveAll(List.of(card1, card2, card3));

        Pageable pageable = PageRequest.of(0, 2);
        Specification<Card> spec = null;

        Page<Card> result = cardRepository.findByUser(user, spec, pageable);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getSize());
    }
}
