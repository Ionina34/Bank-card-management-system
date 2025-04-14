package banks.card.dto.out.card;

import banks.card.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект ответа с информацией о карте")
public class CardResponse {

    @Schema(description = "Идентифиактор карты", example = "123")
    private Long id;

    @Schema(description = "Идентификатор пользователя", example = "12")
    private Long userId;

    @Schema(description = "Зашифрованный номер карты, видны тоько последнии 4 цифры", example = "**** **** **** 1234")
    private String encryptedCardNumber;

    @Schema(description = "Имя владельца карты", example = "IVAN IVANOV")
    private String cardHolder;

    @Schema(description = "Срок годности карты", example = "2030-03-03")
    private LocalDate expiryDate;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Баланс карты", example = "5460")
    private BigDecimal balance;

    @Schema(description = "Дневной лимит карты", example = "10000")
    private BigDecimal dailyLimit;

    @Schema(description = "Месячный лимит карты", example = "null")
    private BigDecimal monthlyLimit;

    @Schema(description = "Лимит на сумму одной танзакции", example = "5000")
    private BigDecimal singleTransactionLimit;

    @Schema(description = "Лимит н кол-во транзакций в день", example = "null")
    private Integer dailyTransactionCountLimit;
}
