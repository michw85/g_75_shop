package de.ait.g_75_shop.security.controller;

import de.ait.g_75_shop.security.dto.LoginRequestDto;
import de.ait.g_75_shop.security.dto.TokenResponseDto;
import de.ait.g_75_shop.security.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }


    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto requestDto) {
        TokenResponseDto tokens = service.login(requestDto);
    }
}
