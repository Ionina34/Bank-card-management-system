package banks.card.service.security;

import banks.card.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для работы с JWT-токенами: генерация, извлечение данных и проверка валидности.
 */
@Service
public class JwtService {

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int HOURS_IN_DAY = 24;
    private static final int MILLISECONDS_IN_SECOND = 1000;
    private static final int TOKEN_EXPIRATION_DAYS = 5;

    /**
     * Извлекает email пользователя из JWT-токена.
     *
     * @param token JWT-токен
     * @return email пользователя
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерирует JWT-токен для пользователя.
     *
     * @param userDetails данные пользователя
     * @return сгенерированный JWT-токен
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("email", customUserDetails.getEmail());
            claims.put("role", customUserDetails.getRole());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Проверяет валидность JWT-токена.
     *
     * @param token       JWT-токен
     * @param userDetails данные пользователя
     * @return {@code true}, если токен валиден, иначе {@code false}
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Извлекает данные из JWT-токена с помощью указанной функции.
     *
     * @param token           JWT-токен
     * @param claimsResolvers функция для извлечения данных
     * @param <T>             тип возвращаемых данных
     * @return извлеченные данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерирует JWT-токен с дополнительными данными.
     *
     * @param extractClaims дополнительные данные для токена
     * @param userDetails   данные пользователя
     * @return сгенерированный JWT-токен
     */
    private String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
        long expirationTimeMillis = System.currentTimeMillis() + MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE * HOURS_IN_DAY * TOKEN_EXPIRATION_DAYS;

        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationTimeMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Проверяет, истек ли срок действия JWT-токена.
     *
     * @param token JWT-токен
     * @return {@code true}, если токен истек, иначе {@code false}
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает дату истечения срока действия JWT-токена.
     *
     * @param token JWT-токен
     * @return дата истечения срока действия
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает все данные (claims) из JWT-токена.
     *
     * @param token JWT-токен
     * @return объект {@link Claims} с данными токена
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Возвращает ключ для подписи JWT-токена.
     *
     * @return ключ подписи
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
