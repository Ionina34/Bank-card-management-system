package banks.card.exception;

import banks.card.dto.out.card.TransferResponse;
import lombok.Getter;

@Getter
public class TransferException extends IllegalStateException{

    TransferResponse response;

    public TransferException(String message, TransferResponse response) {
        super(message);
        this.response = response;
    }
}
