package banks.card.dto.out.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект ответа, содержащий информацию об транзакциях")
public class ListTransactionResponse {

    @Schema(description = "Список транзакций, с подробной информацией о каждой")
    private List<TransactionResponse> responses;
}
