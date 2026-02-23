package de.ait.g_75_shop.security;

import de.ait.g_75_shop.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AuthUserDetails implements UserDetails {

    private final User user;

    public AuthUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Преобразуем роль пользователя в authority Spring Security
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
//        return Collections.singleton(authority); // - если бы было несколько ролей сразу
        return List.of(authority); // возвращаем коллекцию объектов (Set, List...) GrantedAuthority
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Вот здесь мы указываем, что username = email
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        // Используем поле confirmed для активации аккаунта
        return user.isConfirmed();
    }

//    // Дополнительный метод для доступа к оригинальному пользователю
//    public User getUser() {
//        return user;
//    }
}
