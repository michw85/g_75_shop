package de.ait.g_75_shop.service;

import de.ait.g_75_shop.domain.ConfirmationCode;
import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.domain.enums.Role;
import de.ait.g_75_shop.dto.user.UserRegistrationDto;
import de.ait.g_75_shop.exceptions.types.RegistrationException;
import de.ait.g_75_shop.repository.UserRepository;
import de.ait.g_75_shop.security.AuthUserDetails;
import de.ait.g_75_shop.service.interfaces.ConfirmationCodeService;
import de.ait.g_75_shop.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of UserService interface
 * Handles user registration and authentication
 *
 * Реализация интерфейса UserService
 * Обрабатывает регистрацию и аутентификацию пользователей
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailService;
    private final ConfirmationCodeService confirmationCodeService;

    /**
     * Constructor with dependency injection
     * Конструктор с внедрением зависимости
     *
     * @param repository user repository for database operations / репозиторий пользователей для операций с БД
     * @param passwordEncoder password encoder for secure password hashing / кодировщик для безопасного хеширования паролей
     * @param emailService  email service for sending confirmation emails / email сервис для отправки писем подтверждения
     */
    public UserServiceImpl(
            UserRepository repository,
            BCryptPasswordEncoder passwordEncoder,
            EmailServiceImpl emailService, ConfirmationCodeService confirmationCodeService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.confirmationCodeService = confirmationCodeService;
    }

    /**
     * Loads user by email for Spring Security authentication
     *
     * Загружает пользователя по email для аутентификации Spring Security
     *
     * @param email user's email / email пользователя
     * @return UserDetails object for Spring Security / объект UserDetails для Spring Security
     * @throws UsernameNotFoundException if user not found / если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("User with email %s not found", email)
                )
        );

        return new AuthUserDetails(user);
    }

    /**
     * Registers a new user with email confirmation flow
     * Handles three scenarios:
     * 1. First registration - creates new unconfirmed user
     * 2. Re-registration - user exists but not confirmed, updates password
     * 3. Email already confirmed - throws RegistrationException
     *
     * Регистрирует нового пользователя с процессом подтверждения email
     * Обрабатывает три сценария:
     * 1. Первая регистрация - создает нового неподтвержденного пользователя
     * 2. Повторная регистрация - пользователь существует, но не подтвержден, обновляет пароль
     * 3. Email уже подтвержден - выбрасывает RegistrationException
     *
     * @param registrationDto registration data / данные регистрации
     * @throws RegistrationException if email already confirmed / если email уже подтвержден
     */
    @Override
    public void register(UserRegistrationDto registrationDto) {

        // Сценарий 1. пользователь пришёл регистрироваться в первый раз (в БД его ещё нет)
        // Сценарий 2. повторная регистрация. В БД пользователь уже есть - Confirmed(false)
        // Сценарий 3. почта уже есть в БД - Confirmed(true)
        String email = registrationDto.getEmail();
        User user = repository.findByEmail(email).orElse(null);

        if (user == null) {
            // Сценарий 1 (частично) / First registration (part)
            user = new User();
            user.setEmail(email);
            user.setRole(Role.ROLE_USER);
            user.setConfirmed(false);
        } else if (user.isConfirmed()) {
            // Сценарий 3 / Email already confirmed
            throw new RegistrationException(String.format("Email %s already in use", email));
        }
        // Общие действия для сценариев 1 и 2 (Re-registration (user exists but not confirmed))
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setName(registrationDto.getName());

        repository.save(user);

        // Send confirmation email / отправляем email о том, что пользователь должен подтвердить регистрацию
        emailService.sendConfirmationEmail(user);
    }

    @Override
    @Transactional
    public boolean confirmUser(String code) {
        // Find confirmation code
        // Находим код подтверждения
        ConfirmationCode confirmationCode = confirmationCodeService.findByCode(code)
                .orElseThrow(() -> new RegistrationException("Invalid confirmation code"));

        // Check if code is expired
        // Проверяем, не истек ли срок действия кода
        if (confirmationCode.getExpiration().isBefore(LocalDateTime.now())) {
            confirmationCodeService.delete(confirmationCode);
            throw new RegistrationException("Confirmation code has expired");
        }

        // Get user and confirm them
        // Получаем пользователя и подтверждаем его
        User user = confirmationCode.getUser();
        user.setConfirmed(true);
        repository.save(user);

        // Delete used confirmation code
        // Удаляем использованный код подтверждения
        confirmationCodeService.delete(confirmationCode);

        return true;
    }
}
