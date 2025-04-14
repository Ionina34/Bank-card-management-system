package banks.card.config;

import banks.card.web.filter.JwtAuthenticationFilter;
import banks.card.service.services.user.UserUserActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Конфигурационный класс для настройки безопасности приложения.
 * Настраивает Spring Security для использования JWT-аутентификации, CORS,
 * шифрования паролей и управления доступом к ресурсам.
 * Включает поддержку аннотаций безопасности на уровне методов.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    /**
     * Фильтр для обработки JWT-токенов в запросах.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Сервис для загрузки данных пользователей для аутентификации.
     */
    private final UserUserActionService userService;

    /**
     * Настраивает цепочку фильтров безопасности для HTTP-запросов.
     * Отключает CSRF, настраивает CORS, определяет правила авторизации запросов,
     * устанавливает stateless-сессии и добавляет JWT-фильтр перед стандартным фильтром аутентификации.
     *
     * @param http объект {@link HttpSecurity} для конфигурации безопасности
     * @return объект {@link SecurityFilterChain}, представляющий настроенную цепочку фильтров
     * @throws Exception если возникает ошибка при настройке безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "swagger-resources/*", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Создает бин для шифрования паролей с использованием алгоритма BCrypt.
     *
     * @return объект {@link PasswordEncoder} для шифрования и проверки паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создает бин поставщика аутентификации, использующий данные пользователей и шифрование паролей.
     * Настраивает {@link DaoAuthenticationProvider} с сервисом пользователей и кодировщиком паролей.
     *
     * @return объект {@link AuthenticationProvider} для обработки аутентификации
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Создает бин менеджера аутентификации на основе конфигурации Spring Security.
     *
     * @param config объект {@link AuthenticationConfiguration}, содержащий настройки аутентификации
     * @return объект {@link AuthenticationManager} для управления процессом аутентификации
     * @throws Exception если возникает ошибка при получении менеджера аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}