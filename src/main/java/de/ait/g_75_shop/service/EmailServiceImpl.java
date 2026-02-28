package de.ait.g_75_shop.service;

import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.service.interfaces.EmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendConfirmationEmail(User user) {

    }
}
