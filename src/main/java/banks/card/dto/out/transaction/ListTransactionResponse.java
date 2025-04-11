package banks.card.dto.out.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListTransactionResponse {

    private List<TransactionResponse> responses;
}
