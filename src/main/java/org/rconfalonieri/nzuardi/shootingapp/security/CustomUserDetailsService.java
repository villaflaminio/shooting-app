package org.rconfalonieri.nzuardi.shootingapp.security;

import lombok.AllArgsConstructor;
import org.rconfalonieri.nzuardi.shootingapp.exception.BadRequestException;
import org.rconfalonieri.nzuardi.shootingapp.model.PasswordResetToken;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
import org.rconfalonieri.nzuardi.shootingapp.model.UserPrincipal;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ApiResponseDto;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.MailResponse;
import org.rconfalonieri.nzuardi.shootingapp.repository.PasswordResetTokenRepository;
import org.rconfalonieri.nzuardi.shootingapp.repository.UserRepository;
import org.rconfalonieri.nzuardi.shootingapp.security.jwt.TokenProvider;
import org.rconfalonieri.nzuardi.shootingapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;


/**
 * Service to handle user details.
 */
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    private Environment env;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenProvider tokenProvider;

    /**
     * Load user by email.
     * @param email The username.
     * @return The user details.
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email : " + email)
        );

        return UserPrincipal.create(user,user.getActualTesserinoId());
    }

    /**
     * Request a password reset.
     * @param user The user that is requesting the reset.
     * @return The mail response.
     */
    public MailResponse sendMailPostRegistrazione(User user) {
        // Create a new token to reset the password.
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(Instant.now().plusSeconds(Long.parseLong(Objects.requireNonNull(env.getProperty("app.auth.refreshTokenExpiration")))));

        // Save the password reset token
        passwordResetTokenRepository.save(passwordResetToken);
        // Send the email
        Map<String, Object> model = new HashMap<>();
        model.put("name", user.getNome());
        model.put("indirizzo", "http://localhost:8080/" + "user/setPassword?token=" + token );
        return emailService.sendEmail(user.getEmail(),"Shooting App | Imposta la tua password", model, "setPassword");
    }


    public MailResponse sendMailRecoveryPassword(User user) {
        // Create a new token to reset the password.
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(Instant.now().plusSeconds(Long.parseLong(Objects.requireNonNull(env.getProperty("app.auth.refreshTokenExpiration")))));

        // Save the password reset token
        passwordResetTokenRepository.save(passwordResetToken);

        // Send the email
        Map<String, Object> model = new HashMap<>();
        model.put("name", user.getNome());
        model.put("indirizzo", "http://localhost:8080/" + "user/setPassword?token=" + token );
        return emailService.sendEmail(user.getEmail(),"Set new password",model,"src/main/resources/mail-templates/setPassword");
    }

    /**
     * Validate the password reset token.
     * @param token The token to validate.
     * @return invalidToken if the token is invalid, expired if the token is expired
     */
    public String validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> userPasswToken = passwordResetTokenRepository.findByToken(token);
        if(!userPasswToken.isPresent()) {
            throw new BadRequestException("Token non valido");
        }
        final PasswordResetToken passToken = userPasswToken.get();

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    /**
     * Check if the token is found.
     * @param passToken The token to check.
     * @return True if the token is found.
     */
    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    /**
     * Check if the token is expired.
     * @param passToken The token to check.
     * @return True if the token is expired.
     */
    private boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().compareTo(Instant.now()) <= 0;
    }

    /**
     * Request token recovery password.
     * @param token
     * @param user
     * @return
     */
    public ResponseEntity<?> requestTokenRecoveryPassword(String token , User user) {
        String result = validatePasswordResetToken(token);
//        if(result != null) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(false, result));
//        } else {
           // return ResponseEntity.ok(tokenProvider.generateAuthFromUser(user)); :TODO
//        }
    }

    /**
     * Load user by id.
     * @param id The id of the user.
     * @return The UserDetails.
     */
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("User")
        );

        return UserPrincipal.create(user, user.getActualTesserinoId());
    }
}