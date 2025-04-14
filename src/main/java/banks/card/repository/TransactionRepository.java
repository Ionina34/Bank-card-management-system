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

/**
 * Репозиторий {@code TransactionRepository} предоставляет методы для работы с сущностью {@code Transaction} в базе данных.
 * Наследуется от {@code JpaRepository} для базовых операций CRUD и от {@code JpaSpecificationExecutor}
 * для выполнения запросов с использованием спецификаций.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    /**
     * Находит транзакции, связанные с указанной картой, с применением дополнительной спецификации и пагинацией.
     *
     * @param card карта, для которой необходимо найти транзакции
     * @param spec спецификация для дополнительной фильтрации транзакций
     * @param pageable параметры пагинации и сортировки
     * @return страница с найденными транзакциями, удовлетворяющими условиям
     */
    default Page<Transaction> findByCard(Card card, Specification<Transaction> spec, Pageable pageable) {
        Specification<Transaction> cardSpec = (root, query, cb) -> cb.equal(root.get("card"), card);
        Specification<Transaction> combined = cardSpec.and(spec);
        return findAll(combined, pageable);
    }

    /**
     * Находит транзакции для указанной карты, выполненные после заданной даты и соответствующие указанным типам транзакций.
     *
     * @param card карта, для которой выполняются транзакции
     * @param date временная метка, после которой должны быть выполнены транзакции
     * @param types список типов транзакций для фильтрации
     * @return список найденных транзакций
     */
    List<Transaction> findByCardAndTransactionDateAfterAndTransactionTypeIn(Card card, Timestamp date, List<TransactionType> types);

    /**
     * Подсчитывает количество транзакций для указанной карты, выполненных после заданной даты и соответствующих
     * указанным типам транзакций.
     *
     * @param card карта, для которой подсчитываются транзакции
     * @param timestamp временная метка, после которой учитываются транзакции
     * @param types список типов транзакций для фильтрации
     * @return количество транзакций, удовлетворяющих условиям
     */
    long countByCardAndTransactionDateAfterAndTransactionTypeIn(Card card, Timestamp timestamp, List<TransactionType> types);
}
