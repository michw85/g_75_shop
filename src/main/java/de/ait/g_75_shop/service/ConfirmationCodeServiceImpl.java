package de.ait.g_75_shop.service;

import de.ait.g_75_shop.domain.User;
import de.ait.g_75_shop.repository.ConfirmationCodeRepository;
import de.ait.g_75_shop.service.interfaces.ConfirmationCodeService;

public class ConfirmationCodeServiceImpl implements ConfirmationCodeService {

    private final ConfirmationCodeRepository repository;

    public ConfirmationCodeServiceImpl(ConfirmationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public String generateConfirmationCode(User user) {
        return "";
    }
}
