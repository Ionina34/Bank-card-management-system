package banks.card.repository;

import banks.card.entity.Card;
import banks.card.entity.Transaction;
import banks.card.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.sql.Timestamp;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    default Page<Transaction> findByCard(Card card, Specification<Transaction> spec, Pageable pageable) {
        Specification<Transaction> cardSpec = (root, query, cb) -> cb.equal(root.get("card"), card);
        Specification<Transaction> combined = cardSpec.and(spec);
        return findAll(combined, pageable);
    }

    List<Transaction> findByCardAndTransactionDateAfterAndTransactionTypeIn(Card card, Timestamp date, List<TransactionType> types);

    long countByCardAndTransactionDateAfterAndTransactionTypeIn(Card card, Timestamp timestamp, List<TransactionType> types);
}
