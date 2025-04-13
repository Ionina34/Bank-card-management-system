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

@Repository
public interface CardRepository extends JpaRepository<Card, Long> , JpaSpecificationExecutor<Card> {

    default Page<Card> findByUser(User user, Specification<Card> spec, Pageable pageable) {
        Specification<Card> userSpec = (root, query, cb) -> cb.equal(root.get("user"), user);
        Specification<Card> combinedSpec = userSpec.and(spec);
        return findAll(combinedSpec, pageable);
    }
}
