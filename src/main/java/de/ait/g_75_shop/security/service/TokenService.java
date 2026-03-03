package de.ait.g_75_shop.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import java.util.Date;

/**
 * Service for JWT token operations
 * Handles token generation, validation, and claims extraction
 * Uses separate keys for access and refresh tokens
 *
 * Сервис для операций с JWT токенами
 * Обрабатывает генерацию, проверку и извлечение данных из токенов
 * Использует отдельные ключи для access и refresh токенов
 */
@Service
public class TokenService {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    /**
     * Constructor initializing signing keys from base64-encoded phrases
     *
     * Конструктор, инициализирующий ключи подписи из base64-кодированных фраз
     *
     * @param accessPhrase base64-encoded phrase for access token / base64-фраза для access токена
     * @param refreshPhrase base64-encoded phrase for refresh token / base64-фраза для refresh токена
     */
    public TokenService(
            @Value("${KEY_PHRASE_ACCESS}") String accessPhrase,
            @Value("${KEY_PHRASE_REFRESH}") String refreshPhrase
    ){
        // Create signing keys from base64 strings
        // Создаем ключи для подписи токенов из base64 строк
        accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessPhrase));
        refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshPhrase));
    }

    // Timeout values from application.properties (commented out)
    // Таймауты из application.properties (закомментировано)
    /*
    @Value("${jwt.access.expiration:3600000}") // 1 hour default / 1 час по умолчанию
    private Long accessExpiration;
    @Value("${jwt.refresh.expiration:86400000}") // 24 hours default / 24 часа по умолчанию
    private Long refreshExpiration;
     */

    /**
     * Generates access token (short-lived, 15 minutes)
     *
     * Генерирует access токен (короткоживущий, 15 минут)
     *
     * @param email user's email / email пользователя
     * @return JWT access token / JWT access токен
     */
    public String generateAccessToken(String email) {
        return generateToken(email,accessKey, 15*60*1000); // 1 час по умолчанию
    }

    /**
     * Generates refresh token (long-lived, 24 hours)
     *
     * Генерирует refresh токен (долгоживущий, 24 часа)
     *
     * @param email user's email / email пользователя
     * @return JWT refresh token / JWT refresh токен
     */
    public String generateRefreshToken(String email) {
        return generateToken(email,refreshKey, 24*60*60*1000); // 24 часа по умолчанию
    }

    /**
     * Base token generation method
     *
     * Базовый метод создания токена
     *
     * @param email subject (username) / субъект (имя пользователя)
     * @param key signing key / ключ подписи
     * @param expirationMillis expiration time in milliseconds / время истечения в миллисекундах
     * @return JWT token / JWT токен
     */
    public String generateToken(String email, SecretKey key, int expirationMillis){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(email)          // Set username/email / Устанавливаем username/email
                .expiration(expiration)  // Set expiration / Время истечения
                .signWith(key)           // Sign with key / Подписываем ключом
                .compact();              // Build token / Собираем токен
    }

    /**
     * Validates access token
     *
     * Проверяет валидность access токена
     *
     * @param accessToken token to validate / токен для проверки
     * @return true if valid, false otherwise / true если валиден, иначе false
     */
    public boolean validateAccessToken(String accessToken){
        return validateToken(accessToken, accessKey);
    }

    /**
     * Validates refresh token
     * Проверяет валидность refresh токена
     * @param refreshToken token to validate / токен для проверки
     * @return true if valid, false otherwise / true если валиден, иначе false
     */
    public boolean validateRefreshToken(String refreshToken){
        return validateToken(refreshToken, refreshKey);
    }

    /**
     * Generic token validation method
     *
     * Общий метод проверки токена
     *
     * @param token token to validate / токен для проверки
     * @param key signing key / ключ подписи
     * @return true if valid, false otherwise / true если валиден, иначе false
     */
    private boolean validateToken (String token, SecretKey key){
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts claims from access token
     *
     * Извлекает claims из access токена
     *
     * @param accessToken access token / access токен
     * @return claims from token / claims из токена
     */
    public Claims getAccessClaims(String accessToken){
        return getClaims(accessToken, accessKey);
    }

    /**
     * Extracts claims from refresh token
     *
     * Извлекает claims из refresh токена
     *
     * @param refreshToken refresh token / refresh токен
     * @return claims from token / claims из токена
     */
    public Claims getRefreshClaims(String refreshToken){
        return getClaims(refreshToken, refreshKey);
    }


    /**
     * Generic method to extract claims from token
     *
     * Общий метод извлечения claims из токена
     *
     * @param token JWT token / JWT токен
     * @param key signing key / ключ подписи
     * @return claims from token / claims из токена
     */
    private Claims getClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    /**
     * Extracts token value from cookie by name
     *
     * Извлекает значение токена из cookie по имени
     *
     * @param request HTTP request / HTTP запрос
     * @param cookieName name of the cookie / имя cookie
     * @return token value or null if not found / значение токена или null если не найден
     */

    public String getTokenFromRequest (HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
