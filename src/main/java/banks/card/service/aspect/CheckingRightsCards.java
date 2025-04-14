package banks.card.service.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация {@code CheckingRightsCards} используется для обозначения методов, которые требуют проверки прав доступа
 * к двум картам (например, карте отправителя и карте получателя). Применяется на уровне методов и позволяет указать
 * индексы параметров, содержащих идентификатор токена и запрос на операцию.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckingRightsCards {

    /**
     * Индекс параметра метода, содержащего идентификатор токена.
     * По умолчанию равен 0.
     *
     * @return индекс параметра токена
     */
    int tokenIdIndex() default 0;

    /**
     * Индекс параметра метода, содержащего запрос на операцию (например, {@code TransferRequest}).
     * По умолчанию равен 1.
     *
     * @return индекс параметра запроса
     */
    int requestIdIndex() default 1;
}
