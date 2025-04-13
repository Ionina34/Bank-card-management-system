package banks.card.service.specification;

import banks.card.dto.in.filter.CardFilterRequest;
import banks.card.entity.Card;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CardSpecification {

    public static Specification<Card> filterCards(CardFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getNumber() != null && !filter.getNumber().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("encryptedCardNumber")), "%" + filter.getNumber()));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getMinBalance() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("balance"), filter.getMinBalance()));
            }

            if (filter.getMaxBalance() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("balance"), filter.getMaxBalance()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
