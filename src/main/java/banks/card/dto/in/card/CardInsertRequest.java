package banks.card.dto.in.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на созадние карты пользователя")
public class CardInsertRequest {

    @Schema(description = "Номер карты", example = "0000 0000 0000 0000")
    @NotBlank(message = "Номер карты не может быть пустым")
    @Size(min = 19, max = 19, message = "Номер карты должен содержать равно 16 цифр и 3 пробела")
    @Pattern(regexp = "^\\d{4} \\d{4} \\d{4} \\d{4}$", message = "Номер карты должен быть в формате: XXXX XXXX XXXX XXXX")
    private String cardNumber;

    @Schema(description = "Имя и фамилия пользователя", example = "IVAN IVANOV")
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 5, max = 50, message = "Длина имени должна быть от 5 до 50 символов")
    @Pattern(regexp = "[A-Z]{2,}\\s[A-Z]{2,}", message = "Имя пользователя должно быть в формате: IVAN IVANOV")
    private String cardHolder;

    @Schema(description = "Срок действия карты", example = "2025-12-31")
    @NotNull(message = "Дата не может быть пустой")
    @FutureOrPresent(message = "Срок действия карты должен быть текущим или будущем")
    private LocalDate expiryDate;

    @Schema(description = "Баланс карты на момент создания карты", example = "15020.03")
    @Min(value = 0, message = "Баланс должен быть больше 0")
    private BigDecimal balance;
}
