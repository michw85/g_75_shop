package de.ait.g_75_shop.controller;

import de.ait.g_75_shop.dto.user.UserRegistrationDto;
import de.ait.g_75_shop.service.interfaces.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String register(UserRegistrationDto registrationDto) {
        // обращение к сервису
        service.register(registrationDto);
        return "Registration complete. Please check your email.";
    }
}
