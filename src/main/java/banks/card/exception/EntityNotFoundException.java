package banks.card.exception;

/**
 * Исключение {@code EntityNotFoundException} выбрасывается, когда запрашиваемая сущность
 * (например, пользователь или карта) не найдена в системе.
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Конструктор исключения с указанием сообщения об ошибке.
     *
     * @param message сообщение, описывающее причину исключения
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
