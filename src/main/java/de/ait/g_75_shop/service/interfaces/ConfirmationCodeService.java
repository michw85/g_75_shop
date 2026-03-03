package de.ait.g_75_shop.service.interfaces;

import de.ait.g_75_shop.domain.ConfirmationCode;
import de.ait.g_75_shop.domain.User;

import java.util.Optional;

public interface ConfirmationCodeService {

    /**
     * Find confirmation code by value
     * Найти код подтверждения по значению
     *
     * @param code confirmation code value / значение кода подтверждения
     * @return Optional containing confirmation code if found / Optional с кодом подтверждения если найден
     */
    Optional<ConfirmationCode> findByCode(String code);

    /**
     * Delete confirmation code
     * Удалить код подтверждения
     *
     * @param code confirmation code to delete / код подтверждения для удаления
     */
    void delete(ConfirmationCode code);

    String generateConfirmationCode(User user);
}
