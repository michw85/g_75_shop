package de.ait.g_75_shop.security.dto;

public class LoginRequestDto {

    private String email;
    private String password;

    public LoginRequestDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("Login request DTO: email - %s", email);
    }
}
