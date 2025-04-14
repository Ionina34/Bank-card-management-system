package banks.card.exception;

import banks.card.dto.out.card.TransferResponse;
import lombok.Getter;

/**
 * Исключение {@code TransferException} выбрасывается при возникновении ошибки во время выполнения
 * операции перевода средств. Содержит информацию о результате перевода в виде объекта {@code TransferResponse}.
 */
@Getter
public class TransferException extends IllegalStateException{

    /**
     * Ответ, содержащий детали операции перевода.
     */
    TransferResponse response;

    /**
     * Конструктор исключения с указанием сообщения об ошибке и ответа операции перевода.
     *
     * @param message сообщение, описывающее причину исключения
     * @param response объект {@code TransferResponse}, содержащий детали перевода
     */
    public TransferException(String message, TransferResponse response) {
        super(message);
        this.response = response;
    }
}
