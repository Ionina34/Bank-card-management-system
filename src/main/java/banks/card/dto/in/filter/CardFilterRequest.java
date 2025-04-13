package banks.card.dto.in.filter;

import banks.card.entity.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardFilterRequest {

    private String number;
    private CardStatus status;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;
}
