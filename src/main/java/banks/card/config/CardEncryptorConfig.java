package banks.card.config;

import banks.card.utils.CardMascEncryptor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardEncryptorConfig {

    @Value("${hash.card.key}")
    private String secretKey;

    @PostConstruct
    public void init(){
        CardMascEncryptor.setKEY(secretKey);
    }
}
