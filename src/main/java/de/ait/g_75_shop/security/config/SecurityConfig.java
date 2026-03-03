package de.ait.g_75_shop.security.config;

import de.ait.g_75_shop.security.filter.TokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Spring Security configuration class
 * Defines security rules, filters, and authentication mechanisms
 *
 * Класс конфигурации Spring Security
 * Определяет правила безопасности, фильтры и механизмы аутентификации
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security")
    private boolean csrfEnable;

    /**
     * Creates BCrypt password encoder bean
     * Used for password encryption and verification
     *
     * Создает бин для BCrypt кодирования паролей
     * Используется для шифрования и проверки паролей
     *
     * @return BCryptPasswordEncoder instance / экземпляр BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures security filter chain
     * Defines which endpoints are public, which require authentication,
     * and adds JWT token filter
     *
     * Настраивает цепочку фильтров безопасности
     * Определяет, какие endpoint'ы публичные, какие требуют аутентификации,
     * и добавляет JWT токен фильтр
     *
     * @param http HttpSecurity configuration / конфигурация HttpSecurity
     * @param filter JWT token filter / JWT токен фильтр
     * @return configured SecurityFilterChain / настроенная цепочка фильтров безопасности
     * @throws Exception if configuration fails / если конфигурация не удалась
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenFilter filter) throws Exception {
        return http
                // Disable CSRF protection (not needed for REST API with JWT)
        // Отключаем защиту CSRF (не нужна для REST API с JWT)
//                .csrf(AbstractHttpConfigurer::disable) - если оставить token в cookie есть опасность csrf-атаки
                .csrf(x -> x
                        // внутренний репозиторий Spring security для csrf Token
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // исключаем эндпоинты из csrf-защиты
                        .ignoringRequestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/auth/login",
                                "/auth/access",
                                "/auth/logout"
                        )
                )
                // Set session management to stateless (no sessions, each request authenticated separately)
                // Устанавливаем управление сессиями как stateless (без сессий, каждый запрос аутентифицируется отдельно)
                .sessionManagement(
                        x -> x
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//                .httpBasic(Customizer.withDefaults()) - для обычной авторизации с логином и паролем
                // Disable HTTP Basic authentication
                // Отключаем HTTP Basic аутентификацию
                .httpBasic(AbstractHttpConfigurer::disable)
                // Configure authorization rules
                // Настраиваем правила авторизации
                .authorizeHttpRequests(x -> x
                        // Product endpoints
                        // Товары: только ADMIN может создавать
                        .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                        // GET /products - доступно всем
                        .requestMatchers(HttpMethod.GET, "/products").permitAll()
                        // GET /products/{id} - доступно ADMIN и USER
                        .requestMatchers(HttpMethod.GET, "/products/{id:\\d+}").hasAnyRole("ADMIN", "USER")
                        // Authentication/Registration endpoints - public
                        // Эндпоинты аутентификации/регистрации  - публичные
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll() // даем всем доступ к логину
                        .requestMatchers(HttpMethod.POST, "/auth/access").permitAll() // даем всем доступ к авторизации
                        .requestMatchers(HttpMethod.GET, "/auth/csrf").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/logout").permitAll() // даем всем доступ к logout

                        .requestMatchers(HttpMethod.POST, "/users/register").permitAll()

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // All other requests are denied by default
                        // Все остальные запросы по умолчанию запрещены
                        .anyRequest().denyAll()
                )
                // Add JWT token filter before Spring's authentication filter
                // Добавляем JWT токен фильтр перед фильтром аутентификации Spring
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
