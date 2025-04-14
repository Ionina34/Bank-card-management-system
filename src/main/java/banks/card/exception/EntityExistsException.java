package banks.card.exception;

/**
 * Исключение {@code EntityExistsException} выбрасывается, когда пытаются создать сущность,
 * которая уже существует в системе (например, пользователь или карта с одинаковыми уникальными данными).
 */
public class EntityExistsException extends RuntimeException {

    /**
     * Конструктор исключения с указанием сообщения об ошибке.
     *
     * @param message сообщение, описывающее причину исключения
     */
    public EntityExistsException(String message) {
        super(message);
    }
}
