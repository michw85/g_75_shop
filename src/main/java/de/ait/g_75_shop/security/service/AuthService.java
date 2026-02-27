package de.ait.g_75_shop.security.service;

import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.exceptions.types.AuthorizationException;
import de.ait.g_75_shop.security.dto.LoginRequestDto;
import de.ait.g_75_shop.security.dto.TokenResponseDto;
import de.ait.g_75_shop.service.interfaces.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        this.refreshStorage = new ConcurrentHashMap<>();
    }

    public TokenResponseDto login(LoginRequestDto requestDto){
        String email = requestDto.getEmail();
        UserDetails userDetails = userService.loadUserByUsername(email);

        if (passwordEncoder.matches(requestDto.getPassword(), userDetails.getPassword())){
            String accessToken = tokenService.generateAccessToken(email);
            String refreshToken = tokenService.generateRefreshToken(email);
            refreshStorage.put(email, refreshToken);
            return new TokenResponseDto(accessToken, refreshToken);
        } else {
            throw new AuthorizationException("Password is incorrect");
        }
    }
}
