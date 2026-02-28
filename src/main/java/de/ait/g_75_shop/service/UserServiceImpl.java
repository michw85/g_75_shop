package de.ait.g_75_shop.service;

import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.domain.enums.Role;
import de.ait.g_75_shop.dto.user.UserRegistrationDto;
import de.ait.g_75_shop.exceptions.types.RegistrationException;
import de.ait.g_75_shop.repository.UserRepository;
import de.ait.g_75_shop.security.AuthUserDetails;
import de.ait.g_75_shop.service.interfaces.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailService;

    public UserServiceImpl(
            UserRepository repository,
            BCryptPasswordEncoder passwordEncoder,
            EmailServiceImpl emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("User with email %s not found", email)
                )
        );

        return new AuthUserDetails(user);
    }


    @Override
    public void register(UserRegistrationDto registrationDto) {

        // Сценарий 1. пользователь пришёл регистрироваться в первый раз (в БД его ещё нет)
        // Сценарий 2. повторная регистрация. В БД пользователь уже есть - Confirmed(false)
        // Сценарий 3. почта уже есть в БД - Confirmed(true)
        String email = registrationDto.getEmail();
        User user = repository.findByEmail(email).orElse(null);

        if (user == null) {
            // Сценарий 1 (частично)
            user = new User();
            user.setEmail(email);
            user.setRole(Role.ROLE_USER);
            user.setConfirmed(false);
        } else if (user.isConfirmed()) {
            // Сценарий 3
            throw new RegistrationException(String.format("Email %s already in use", email));
        }
        // Общие действия для сценариев 1 и 2
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setName(registrationDto.getName());

        repository.save(user);
        // отправляем email о том, что пользователь должен подтвердить регистрацию
        emailService.sendConfirmationEmail(user);
    }
}
