package de.ait.g_75_shop.repository;

import de.ait.g_75_shop.domain.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {
    /**
     * Find confirmation code by its value
     * Найти код подтверждения по его значению
     *
     * @param value confirmation code value / значение кода подтверждения
     * @return Optional containing confirmation code if found / Optional с кодом подтверждения если найден
     */
    Optional<ConfirmationCode> findByValue(String value);
}
