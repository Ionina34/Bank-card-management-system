package banks.card.web.filter;

import banks.card.service.security.JwtService;
import banks.card.service.services.user.UserUserActionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static banks.card.service.security.JwtService.BEARER_PREFIX;
import static banks.card.service.security.JwtService.HEADER_NAME;

/**
 * Фильтр для аутентификации запросов с использованием JWT-токена.
 * Проверяет наличие и валидность JWT-токена в заголовке запроса,
 * извлекает данные пользователя и устанавливает аутентификацию в контексте безопасности Spring Security.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserUserActionService userService;

    /**
     * Обрабатывает входящий HTTP-запрос, проверяя JWT-токен и выполняя аутентификацию.
     * Если токен присутствует, валиден и пользователь не аутентифицирован,
     * создается аутентификационный объект и устанавливается в контекст безопасности.
     * Затем запрос передается следующему фильтру в цепочке.
     *
     * @param request     HTTP-запрос, содержащий заголовок с JWT-токеном
     * @param response    HTTP-ответ
     * @param filterChain цепочка фильтров для дальнейшей обработки запроса
     * @throws ServletException если возникает ошибка обработки запроса
     * @throws IOException      если возникает ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_NAME);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(BEARER_PREFIX.length());
        String email = jwtService.extractEmail(jwt);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(email);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}