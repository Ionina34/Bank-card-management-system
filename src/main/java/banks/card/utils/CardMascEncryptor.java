package banks.card.utils;

import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Утилитный класс для шифрования и дешифрования номеров карт с использованием алгоритма AES.
 */
@UtilityClass
public class CardMascEncryptor {

    private static final String ALGORITHM = "AES";
    private static String KEY;

    /**
     * Устанавливает ключ шифрования для алгоритма AES.
     *
     * @param KEY строка, представляющая ключ шифрования
     */
    public static void setKEY(String KEY) {
        CardMascEncryptor.KEY = KEY;
    }

    /**
     * Шифрует номер карты с использованием алгоритма AES.
     * Результат шифрования кодируется в строку Base64.
     *
     * @param cardNumber номер карты для шифрования
     * @return зашифрованная строка в формате Base64
     * @throws Exception если произошла ошибка при шифровании
     */
    public String encrypt(String cardNumber) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(cardNumber.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Расшифровывает зашифрованный номер карты.
     * Входная строка должна быть в формате Base64.
     *
     * @param encryptedCard зашифрованный номер карты в формате Base64
     * @return расшифрованный номер карты
     * @throws Exception если произошла ошибка при дешифровании
     */
    public String decrypt(String encryptedCard) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedCard));
        return new String(decrypted);
    }
}
