package banks.card.dto.out.card;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ListCardResponse {

    private List<CardResponse> cards;
}
