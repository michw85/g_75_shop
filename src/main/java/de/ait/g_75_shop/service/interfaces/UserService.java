package de.ait.g_75_shop.service.interfaces;

import de.ait.g_75_shop.dto.user.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void register(UserRegistrationDto registrationDto);
    /**
     * Confirm user registration by confirmation code
     * Подтвердить регистрацию пользователя по коду подтверждения
     *
     * @param code confirmation code / код подтверждения
     * @return true if confirmation successful / true если подтверждение успешно
     */
    boolean confirmUser(String code);
}
