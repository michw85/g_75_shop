package de.ait.g_75_shop.security.filter;

import de.ait.g_75_shop.security.service.TokenService;
import de.ait.g_75_shop.service.interfaces.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static de.ait.g_75_shop.constants.Constants.ACCESS_TOKEN_COOKIE_NAME;

/**
 * JWT token filter that intercepts each request
 * Extracts token from cookie, validates it, and sets authentication in SecurityContext
 * Extends OncePerRequestFilter to ensure single execution per request
 *
 * JWT токен фильтр, перехватывающий каждый запрос
 * Извлекает токен из cookie, проверяет его и устанавливает аутентификацию в SecurityContext
 * Расширяет OncePerRequestFilter для гарантии однократного выполнения на запрос
 */
@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserService userService;

    public TokenFilter(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    /**
     * Filters each request to authenticate based on JWT token
     *
     * Фильтрует каждый запрос для аутентификации на основе JWT токена
     *
     * @param request HTTP request / HTTP запрос
     * @param response HTTP response / HTTP ответ
     * @param filterChain filter chain / цепочка фильтров
     * @throws ServletException if servlet error occurs / если ошибка сервлета
     * @throws IOException if I/O error occurs / если ошибка ввода/вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extract access token from cookie
        // Извлекаем access токен из cookie
        String accessToken = tokenService.getTokenFromRequest(request, ACCESS_TOKEN_COOKIE_NAME);

        // If token exists and is valid, authenticate the user
        // Если токен существует и валиден, аутентифицируем пользователя
        if (accessToken != null & tokenService.validateAccessToken(accessToken)) {
            // Get claims from token
            // Получаем claims из токена
            Claims claims = tokenService.getAccessClaims(accessToken);
            String email = claims.getSubject();
            // Load user details from database
            // Загружаем детали пользователя из базы данных
            UserDetails userDetails = userService.loadUserByUsername(email);

            // Create authentication token
            // Создаем токен аутентификации
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            // Set authentication in SecurityContext
            // Устанавливаем аутентификацию в SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Continue filter chain
        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
}
