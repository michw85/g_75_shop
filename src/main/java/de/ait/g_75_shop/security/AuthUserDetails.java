package de.ait.g_75_shop.security;

import de.ait.g_75_shop.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapter class that integrates our User entity with Spring Security
 * Implements UserDetails interface required by Spring Security
 *
 * Адаптер, который интегрирует нашу сущность User с Spring Security
 * Реализует интерфейс UserDetails, требуемый Spring Security
 */
public class AuthUserDetails implements UserDetails {

    private final User user;

    /**
     * Constructor wrapping our User entity
     * Конструктор, оборачивающий нашу сущность User
     *
     * @param user our domain user / наш доменный пользователь
     */
    public AuthUserDetails(User user) {
        this.user = user;
    }

    /**
     * Returns user's authorities (roles)
     * Converts our Role enum to Spring Security GrantedAuthority
     *
     * Возвращает права пользователя (роли)
     * Преобразует наш enum Role в GrantedAuthority Spring Security
     *
     * @return collection of granted authorities / коллекция предоставленных прав
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert user's role to Spring Security authority / Преобразуем роль пользователя в authority Spring Security
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
//        return Collections.singleton(authority); // - если бы было несколько ролей сразу
        return List.of(authority); // return collection of GrantedAuthority objects / возвращаем коллекцию объектов (Set, List...) GrantedAuthority
    }

    /**
     * Returns user's password (already encrypted)
     * Возвращает пароль пользователя (уже зашифрованный)
     *
     * @return encrypted password / зашифрованный пароль
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns username (email in our system)
     * Возвращает имя пользователя (email в нашей системе)
     *
     * @return email as username / email как имя пользователя
     */
    @Override
    public String getUsername() {
        // Here we specify that username = email
        // Вот здесь мы указываем, что username = email
        return user.getEmail();
    }

    /**
     * Checks if account is enabled (email confirmed)
     * Проверяет, активирована ли учетная запись (подтвержден email)
     *
     * @return true if confirmed, false otherwise / true если подтвержден, иначе false
     */
    @Override
    public boolean isEnabled() {
        // CRITICAL FIX: Only confirmed users can log in
        // КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ: Только подтвержденные пользователи могут войти
        return user.isConfirmed();
    }

//    // Дополнительный метод для доступа к оригинальному пользователю
//    public User getUser() {
//        return user;
//    }

    // Other UserDetails methods return true by default
    // Остальные методы UserDetails возвращают true по умолчанию

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
