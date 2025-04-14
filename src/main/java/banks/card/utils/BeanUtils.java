package banks.card.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

/**
 * Утилитный класс для работы с объектами Java Bean.
 * Предоставляет методы для копирования свойств между объектами.
 */
@UtilityClass
public class BeanUtils {

    /**
     * Копирует ненулевые свойства из исходного объекта в целевой объект.
     * Использует рефлексию для доступа к полям объекта.
     * Поля с null-значениями в исходном объекте игнорируются.
     *
     * @param source      исходный объект, из которого копируются свойства
     * @param destination целевой объект, в который копируются свойства
     * @throws IllegalAccessException если доступ к полю невозможен
     */
    @SneakyThrows
    public void copyNotNullProperties(Object source, Object destination) {
        Class<?> clazz = source.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);

            if (value != null) {
                field.set(destination, value);
            }
        }
    }
}
