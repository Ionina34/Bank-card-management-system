package banks.card.exception;

import banks.card.dto.out.card.WithdrawalResponse;
import lombok.Getter;

@Getter
public class WithdrawalException extends IllegalStateException {

    WithdrawalResponse response;

    public WithdrawalException(String message, WithdrawalResponse response) {
        super(message);
        this.response = response;
    }
}
