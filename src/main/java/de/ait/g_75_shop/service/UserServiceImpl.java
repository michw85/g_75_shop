package de.ait.g_75_shop.service;

import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.repository.UserRepository;
import de.ait.g_75_shop.security.AuthUserDetails;
import de.ait.g_75_shop.service.interfaces.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
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
}
