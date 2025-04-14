package banks.card.service.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация {@code CheckingRightsCard} используется для обозначения методов, которые требуют проверки прав доступа
 * к карте. Применяется на уровне методов и позволяет указать индексы параметров, содержащих идентификатор токена
 * и идентификатор карты.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckingRightsCard {

    /**
     * Индекс параметра метода, содержащего идентификатор токена.
     * По умолчанию равен 0.
     *
     * @return индекс параметра токена
     */
    int tokenIdIndex() default 0;

    /**
     * Индекс параметра метода, содержащего идентификатор карты.
     * По умолчанию равен 1.
     *
     * @return индекс параметра карты
     */
    int cardIdIndex() default 1;
}
