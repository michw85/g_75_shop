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

@Service
public class TokenService {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    public TokenService(
            @Value("${KEY_PHRASE_ACCESS}") String accessPhrase,
            @Value("${KEY_PHRASE_REFRESH}") String refreshPhrase
    ){
        // Создаем ключи для подписи токенов
        accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessPhrase));
        refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshPhrase));
    }

    // Таймауты из application.properties
    /*
    @Value("${jwt.access.expiration:3600000}") // 1 час по умолчанию
    private Long accessExpiration;
    @Value("${jwt.refresh.expiration:86400000}") // 24 часа по умолчанию
    private Long refreshExpiration;
     */

    // 1. Генерация access токена
    public String generateAccessToken(String email) {
        return generateToken(email,accessKey, 15*60*1000); // 1 час по умолчанию
    }
    // 2. Генерация refresh токена
    public String generateRefreshToken(String email) {
        return generateToken(email,refreshKey, 24*60*60*1000); // 24 часа по умолчанию
    }

    // 3. Базовый метод создания токена
    public String generateToken(String email, SecretKey key, int expirationMillis){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(email)          // Устанавливаем username/email
                .expiration(expiration)  // Время истечения
                .signWith(key)           // Подписываем ключом
                .compact();              // Собираем токен
    }

    // Проверка валидности access токена
    public boolean validateAccessToken(String accessToken){
        return validateToken(accessToken, accessKey);
    }

    // Проверка валидности refresh токена
    public boolean validateRefreshToken(String refreshToken){
        return validateToken(refreshToken, refreshKey);
    }

    // Проверка валидности токена
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

    public Claims getAccessClaims(String accessToken){
        return getClaims(accessToken, accessKey);
    }

    public Claims getRefreshClaims(String refreshToken){
        return getClaims(refreshToken, refreshKey);
    }


    // Извлечение всех claims
    private Claims getClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

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
