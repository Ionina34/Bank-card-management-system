package banks.card.config;

import banks.card.utils.CardMascEncryptor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки шифрования данных карт.
 * Устанавливает секретный ключ для {@link CardMascEncryptor} на основе значения,
 * полученного из свойств приложения.
 */
@Configuration
public class CardEncryptorConfig {

    /**
     * Секретный ключ для шифрования данных карт.
     * Значение берется из свойства приложения с именем <code>hash.card.key</code>.
     */
    @Value("${hash.card.key}")
    private String secretKey;

    /**
     * Инициализирует {@link CardMascEncryptor} с использованием секретного ключа.
     * Вызывается автоматически после создания бина для установки ключа шифрования.
     */
    @PostConstruct
    public void init() {
        CardMascEncryptor.setKEY(secretKey);
    }
}