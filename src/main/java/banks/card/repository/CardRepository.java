package banks.card.repository;

import banks.card.entity.Card;
import banks.card.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий {@code CardRepository} предоставляет методы для работы с сущностью {@code Card} в базе данных.
 * Наследуется от {@code JpaRepository} для базовых операций CRUD и от {@code JpaSpecificationExecutor}
 * для выполнения запросов с использованием спецификаций.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> , JpaSpecificationExecutor<Card> {

    /**
     * Находит карты, принадлежащие указанному пользователю, с применением дополнительной спецификации и пагинацией.
     *
     * @param user пользователь, чьи карты необходимо найти
     * @param spec спецификация для дополнительной фильтрации карт
     * @param pageable параметры пагинации и сортировки
     * @return страница с найденными картами, удовлетворяющими условиям
     */
    default Page<Card> findByUser(User user, Specification<Card> spec, Pageable pageable) {
        Specification<Card> userSpec = (root, query, cb) -> cb.equal(root.get("user"), user);
        Specification<Card> combinedSpec = userSpec.and(spec);
        return findAll(combinedSpec, pageable);
    }
}
