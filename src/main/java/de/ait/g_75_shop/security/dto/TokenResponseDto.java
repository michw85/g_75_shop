package de.ait.g_75_shop.security.dto;

/**
 * DTO for token response
 * Contains JWT tokens returned after authentication
 *
 * DTO для ответа с токенами
 * Содержит JWT токены, возвращаемые после аутентификации
 */
public class TokenResponseDto {

    private String accessToken;
    private String refreshToken;

    /**
     * Default constructor
     * Конструктор по умолчанию
     */
    public TokenResponseDto() {
    }

    /**
     * Constructor for both access and refresh tokens
     * Конструктор для обоих токенов (access и refresh)
     */
    public TokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /**
     * Constructor for access token only (when refreshing)
     * Конструктор только для access токена (при обновлении)
     */
    public TokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
