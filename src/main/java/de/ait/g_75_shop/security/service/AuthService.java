package de.ait.g_75_shop.security.service;

import de.ait.g_75_shop.constants.Constants;
import de.ait.g_75_shop.exceptions.types.AuthorizationException;
import de.ait.g_75_shop.security.dto.LoginRequestDto;
import de.ait.g_75_shop.security.dto.TokenResponseDto;
import de.ait.g_75_shop.service.interfaces.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.ait.g_75_shop.constants.Constants.REFRESH_TOKEN_COOKIE_NAME;

/**
 * Service for authentication operations
 * Handles login, token refresh, and logout
 * Uses in-memory storage for refresh tokens (ConcurrentHashMap)
 *
 * Сервис для операций аутентификации
 * Обрабатывает вход, обновление токена и выход
 * Использует in-memory хранилище для refresh токенов (ConcurrentHashMap)
 */
@Service
public class AuthService {

    private final UserService userService;
    private  final BCryptPasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final Map<String, String> refreshStorage;

    public AuthService(UserService userService, BCryptPasswordEncoder passwordEncoder, TokenService tokenService, Map<String, String> refreshStorage) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        // Thread-safe map for storing refresh tokens
        // Потокобезопасная карта для хранения refresh токенов
        this.refreshStorage = new ConcurrentHashMap<>();
    }

    /**
     * Authenticates user and generates token pair
     *
     * Аутентифицирует пользователя и генерирует пару токенов
     *
     * @param requestDto login credentials / учетные данные
     * @return access and refresh tokens / access и refresh токены
     * @throws AuthorizationException if credentials are invalid / если учетные данные неверны
     */
    public TokenResponseDto login(LoginRequestDto requestDto){
        String email = requestDto.getEmail();
        UserDetails userDetails = userService.loadUserByUsername(email);

        // Check if user is enabled (confirmed) / Проверяем, активирован ли пользователь (подтвержден)
        if (!userDetails.isEnabled()) {
            throw new AuthorizationException("Account not confirmed. Please check your email.");
        }

        // Verify password / Проверяем пароль
        if (passwordEncoder.matches(requestDto.getPassword(), userDetails.getPassword())){
            // Generate tokens / Генерируем токены
            String accessToken = tokenService.generateAccessToken(email);
            String refreshToken = tokenService.generateRefreshToken(email);

            // Store refresh token for future validation / Сохраняем refresh токен для будущей проверки
            refreshStorage.put(email, refreshToken);
            return new TokenResponseDto(accessToken, refreshToken);
        } else {
            throw new AuthorizationException("Password is incorrect");
        }
    }

    /**
     * Generates new access token using valid refresh token
     *
     * Генерирует новый access токен используя валидный refresh токен
     *
     * @param request HTTP request containing refresh token cookie / HTTP запрос с refresh токеном
     * @return new access token / новый access токен
     * @throws AuthorizationException if refresh token is invalid / если refresh токен недействителен
     */
    public TokenResponseDto getAccessToken(HttpServletRequest request) {
        // Extract refresh token from cookie / Извлекаем refresh токен из cookie
        String refreshToken = tokenService.getTokenFromRequest(request, REFRESH_TOKEN_COOKIE_NAME);

        // Validate refresh token / Проверяем refresh токен
        if (refreshToken != null && tokenService.validateRefreshToken(refreshToken)) {
            Claims claims = tokenService.getRefreshClaims(refreshToken);
            String email = claims.getSubject();

            // Verify user is still confirmed / Проверяем, что пользователь все еще подтвержден
            UserDetails userDetails = userService.loadUserByUsername(email);
            if (!userDetails.isEnabled()) {
                throw new AuthorizationException("Account is not confirmed");
            }

            // Check if stored refresh token matches / Проверяем соответствие сохраненного refresh токена
            String saveRefreshToken = refreshStorage.get(email);

            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)){
                // Generate new access token / Генерируем новый access токен
                String accessToken = tokenService.generateAccessToken(email);
                return new TokenResponseDto(accessToken);
            }
        }
        throw new AuthorizationException("Refresh token is invalid");
    }

    /**
     * Removes user's refresh token from storage (logout)
     *
     * Удаляет refresh токен пользователя из хранилища (выход)
     *
     * @param request HTTP request containing refresh token cookie / HTTP запрос с refresh токеном
     */
    public void removeUserRefreshToken (HttpServletRequest request) {
        // Extract refresh token from cookie / Извлекаем refresh токен из cookie
        String refreshToken = tokenService.getTokenFromRequest(request, REFRESH_TOKEN_COOKIE_NAME);

        // If token exists and is valid, remove it from storage / Если токен существует и валиден, удаляем его из хранилища
        if (refreshToken != null && tokenService.validateRefreshToken(refreshToken)) {
            Claims claims = tokenService.getRefreshClaims(refreshToken);
            String email = claims.getSubject();

            refreshStorage.remove(email);
        }
    }
}
