package de.ait.g_75_shop.service.interfaces;

import de.ait.g_75_shop.domain.User;

public interface EmailService {
    void sendConfirmationEmail(User user);
}
