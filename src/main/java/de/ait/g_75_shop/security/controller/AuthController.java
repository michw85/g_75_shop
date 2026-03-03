package de.ait.g_75_shop.security.controller;

import de.ait.g_75_shop.security.dto.LoginRequestDto;
import de.ait.g_75_shop.security.dto.TokenResponseDto;
import de.ait.g_75_shop.security.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import static de.ait.g_75_shop.constants.Constants.ACCESS_TOKEN_COOKIE_NAME;
import static de.ait.g_75_shop.constants.Constants.REFRESH_TOKEN_COOKIE_NAME;

/**
 * Authentication controller
 * Handles login, token refresh, and logout operations
 * Uses cookies for token storage (HttpOnly for security)
 *
 * Контроллер аутентификации
 * Обрабатывает операции входа, обновления токена и выхода
 * Использует cookies для хранения токенов (HttpOnly для безопасности)
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    /**
     * Authenticates user and sets access/refresh tokens as HttpOnly cookies
     * POST /auth/login
     *
     * Аутентифицирует пользователя и устанавливает access/refresh токены как HttpOnly cookies
     *
     * @param requestDto login credentials (email/password) / учетные данные для входа
     * @param response HTTP response to set cookies / HTTP ответ для установки cookies
     */
    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        // Get tokens from service / Получаем токены от сервиса
        TokenResponseDto tokens = service.login(requestDto);

        // Create and set access token cookie / Создаем и устанавливаем cookie с access токеном
        Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, tokens.getAccessToken());
        accessCookie.setPath("/"); // Cookie available for all paths / Cookie доступен для всех путе
        accessCookie.setHttpOnly(true); // Prevents XSS attacks / Предотвращает XSS атаки
        response.addCookie(accessCookie);

        // Create and set refresh token cookie / Создаем и устанавливаем cookie с refresh токеном
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, tokens.getRefreshToken());
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
    }

    /**
     * Gets new access token using refresh token
     * POST /auth/access
     *
     * Получает новый access токен используя refresh токен
     *
     * @param request HTTP request containing refresh token cookie / HTTP запрос с refresh токеном в cookie
     * @param response HTTP response to set new access token cookie / HTTP ответ для установки нового access токена
     */
    @PostMapping("/access")
    public void getNewAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // Get new tokens from service / Получаем новые токены от сервиса
        TokenResponseDto tokens = service.getAccessToken(request);

        // Create and set new access token cookie / Создаем и устанавливаем новый access токен
        Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, tokens.getAccessToken());
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        response.addCookie(accessCookie);
    }

    /**
     * Logs out user by removing refresh token from storage and clearing cookies
     * POST /auth/logout
     *
     * Выполняет выход пользователя: удаляет refresh токен из хранилища и очищает cookies
     *
     * @param request HTTP request containing refresh token cookie / HTTP запрос с refresh токеном в cookie
     * @param response HTTP response to clear cookies / HTTP ответ для очистки cookies
     */
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Remove refresh token from server storage / Удаляем refresh токен из хранилища на сервере
        service.removeUserRefreshToken(request);

        // Clear access token cookie (set maxAge to 0) / Очищаем cookie access токена (устанавливаем maxAge = 0)
        Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, null);
        accessCookie.setPath("/");
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        // Clear refresh token cookie / Очищаем cookie refresh токена
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        accessCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    // Отправка клиенту Csrf Token (Spring security)
    @GetMapping("/csrf")
    public CsrfToken csrfToken(CsrfToken csrfToken) {
        return csrfToken;
    }
}
