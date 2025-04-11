package banks.card.repository;

import banks.card.entity.Card;
import banks.card.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByCard(Card card, Pageable pageable);
}
