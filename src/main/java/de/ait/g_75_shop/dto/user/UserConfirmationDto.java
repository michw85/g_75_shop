package de.ait.g_75_shop.dto.user;

/**
 * DTO for user confirmation request
 * Contains confirmation code from email link
 *
 * DTO для запроса подтверждения пользователя
 * Содержит код подтверждения из ссылки в email
 */
public class UserConfirmationDto {

    private String code;

    public UserConfirmationDto() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}