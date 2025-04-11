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
     * Извлечение имени пользователя из токена.
     *
     * @param token Токен доступа
     * @return Email пользователя
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерация токена
     *
     * @param userDetails Данные пользователя
     * @return Токен доступа
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
     * Проверка токена на валидность
     *
     * @param token       Токен доступа
     * @param userDetails Данные  пользователя
     * @return true, если токен валиден
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Извлечение данных из токена
     *
     * @param token           Токен доступа
     * @param claimsResolvers Функция извлечения данных
     * @param <T>             Тип данных
     * @return Данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерация токена доступа
     *
     * @param extractClaims Допольнительные данные
     * @param userDetails   Данные пользователя
     * @return Токен доступа
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
     * Проверка токена на просроченность
     *
     * @param token Токен доступа
     * @return true, если токен просрочен
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлечение даты истечения токена
     *
     * @param token Токен доступа
     * @return Дата истечения
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Тзвлечение всех данных из токена доступа
     *
     * @param token Токен доступа
     * @return Данные
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Получение ключа для подписи токена доступа
     *
     * @return Ключ
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
