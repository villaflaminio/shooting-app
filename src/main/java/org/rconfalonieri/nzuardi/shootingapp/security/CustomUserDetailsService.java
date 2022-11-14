package org.rconfalonieri.nzuardi.shootingapp.security;

import lombok.AllArgsConstructor;
import org.rconfalonieri.nzuardi.shootingapp.exception.BadRequestException;
import org.rconfalonieri.nzuardi.shootingapp.model.PasswordResetToken;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
//import org.rconfalonieri.nzuardi.shootingapp.model.UserPrincipal;
import org.rconfalonieri.nzuardi.shootingapp.model.UserPrincipal;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ApiResponseDto;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.MailResponse;
import org.rconfalonieri.nzuardi.shootingapp.repository.PasswordResetTokenRepository;
import org.rconfalonieri.nzuardi.shootingapp.repository.UserRepository;
import org.rconfalonieri.nzuardi.shootingapp.security.helper.UserHelper;
import org.rconfalonieri.nzuardi.shootingapp.security.jwt.TokenProvider;
import org.rconfalonieri.nzuardi.shootingapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Service to handle user details.
 */
@Service
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


    @Value("${appAuth.resetPasswordExpiration}")
    private String refreshTokenExpiration;

    @Value("${serverProd.uri}")
    private String uri;

    /**
     * Load user by email.
     * @return The user details.
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        if(username.contains("@")){

            return  userRepository.findByEmail(username)
                    .map(this::createSpringSecurityUser)
                    .orElseThrow(() -> new ResourceNotFoundException("User " +username+ " was not found in the database"));

        }else{
            long tesserinoId = Long.parseLong(username);
            return userRepository.findByActualTesserinoId(tesserinoId)
                    .map(this::createSpringSecurityUser)
                    .orElseThrow(() -> new ResourceNotFoundException("User with tesserinoId" + username + " was not found in the database"));
        }

    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                grantedAuthorities);
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
        passwordResetToken.setExpiryDate(Instant.now().plusSeconds(Long.parseLong(refreshTokenExpiration)));

        // Save the password reset token
        passwordResetTokenRepository.save(passwordResetToken);
        // Send the email
        Map<String, Object> model = new HashMap<>();
        model.put("name", user.getNome());
        model.put("indirizzo", uri + "user/setPassword?token=" + token );

        if(user.getAuthorities().stream().anyMatch(a -> a.getName().equals("ROLE_USER")))
            return emailService.sendEmail(user.getEmail(),"Shooting App | Imposta la tua password", model, "setPasswordUtente");

        return emailService.sendEmail(user.getEmail(),"Shooting App | Imposta la tua password", model, "setPasswordAdmin&Istruttori");
    }


    public MailResponse sendMailRecoveryPassword(User user) {
        // Create a new token to reset the password.
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(Instant.now().plusSeconds(5555555l));

        // Save the password reset token
        passwordResetTokenRepository.save(passwordResetToken);
        //Todo template per recupero password
        // Send the email
        Map<String, Object> model = new HashMap<>();
        model.put("name", user.getNome());
        model.put("indirizzo", "http://localhost:8080/" + "user/setPassword?token=" + token );
        return emailService.sendEmail(user.getEmail(),"Set new password",model,"setPassword");
    }







}