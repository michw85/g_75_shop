package de.ait.g_75_shop.constants;

/**
 * Application-wide constants interface
 * Contains constant values used across the application
 *
 * Интерфейс с константами приложения
 * Содержит постоянные значения, используемые во всем приложении
 */
public interface Constants {

    /**
     * Cookie name for JWT access token
     * Имя cookie для JWT access токена
     */
    String ACCESS_TOKEN_COOKIE_NAME = "Access-Token";

    /**
     * Cookie name for JWT refresh token
     * Имя cookie для JWT refresh токена
     */
    String REFRESH_TOKEN_COOKIE_NAME = "Refresh-Token";
}
