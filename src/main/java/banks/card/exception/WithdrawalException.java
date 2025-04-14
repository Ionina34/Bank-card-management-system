package banks.card.exception;

import banks.card.dto.out.card.WithdrawalResponse;
import lombok.Getter;

/**
 * Исключение {@code WithdrawalException} выбрасывается при возникновении ошибки во время выполнения
 * операции снятия средств с карты. Содержит информацию о результате операции в виде объекта {@code WithdrawalResponse}.
 */
@Getter
public class WithdrawalException extends IllegalStateException {

    /**
     * Ответ, содержащий детали операции снятия средств.
     */
    WithdrawalResponse response;

    /**
     * Конструктор исключения с указанием сообщения об ошибке и ответа операции снятия.
     *
     * @param message сообщение, описывающее причину исключения
     * @param response объект {@code WithdrawalResponse}, содержащий детали операции
     */
    public WithdrawalException(String message, WithdrawalResponse response) {
        super(message);
        this.response = response;
    }
}
