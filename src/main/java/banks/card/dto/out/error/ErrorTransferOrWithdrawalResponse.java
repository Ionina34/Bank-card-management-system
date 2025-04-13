package banks.card.dto.out.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorTransferOrWithdrawalResponse<T> {

    String message;
    T response;
}
