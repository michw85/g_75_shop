package de.ait.g_75_shop.service;

import de.ait.g_75_shop.domain.ConfirmationCode;
import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.repository.ConfirmationCodeRepository;
import de.ait.g_75_shop.service.interfaces.ConfirmationCodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationCodeServiceImpl implements ConfirmationCodeService {

    private final ConfirmationCodeRepository repository;

    public ConfirmationCodeServiceImpl(ConfirmationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public String generateConfirmationCode(User user) {
        String value = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusHours(24); // текущий момент времени + сутки
        ConfirmationCode entity = new ConfirmationCode(value, expiration,user);
        repository.save(entity);
        return value;
    }
}
