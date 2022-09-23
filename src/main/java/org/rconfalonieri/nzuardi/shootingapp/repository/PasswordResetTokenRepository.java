package org.rconfalonieri.nzuardi.shootingapp.repository;

import org.rconfalonieri.nzuardi.shootingapp.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;

/**
 * Repository to manage all the operations related to the password reset token.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Transactional
    void deleteAllByExpiryDateIsLessThan(Instant expiryDate);

    @Modifying
    @Transactional
    void deleteByExpiryDateIsLessThan(Instant expiryDate);
}