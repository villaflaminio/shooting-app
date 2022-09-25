package org.rconfalonieri.nzuardi.shootingapp.controller;

import org.rconfalonieri.nzuardi.shootingapp.exception.BadRequestException;
import org.rconfalonieri.nzuardi.shootingapp.model.PasswordResetToken;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ApiResponseDto;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.LoginDTO;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.LoginUserDTO;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.UserDTO;
import org.rconfalonieri.nzuardi.shootingapp.repository.PasswordResetTokenRepository;
import org.rconfalonieri.nzuardi.shootingapp.repository.UserRepository;
import org.rconfalonieri.nzuardi.shootingapp.security.CustomUserDetailsService;
import org.rconfalonieri.nzuardi.shootingapp.security.helper.UserHelper;
import org.rconfalonieri.nzuardi.shootingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Controller with the REST endpoints for authentication and user management.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserHelper userHelper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @CrossOrigin(origins = "*")
    @PostMapping("/login/user")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginUserDTO loginUserDTO) {
        return userHelper.authorizeUser(loginUserDTO);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDto) {
        return userHelper.authorize(loginDto);
    }

    @GetMapping("/registerFirstUser")
    public ResponseEntity<?> registerUser() {
         boolean status = userHelper.registerFirstUser();
            if (status) {
                return ResponseEntity.ok(new ApiResponseDto(true, "User registered successfully"));
            } else {
                return ResponseEntity.ok(new ApiResponseDto(false, "User already registered"));
            }
    }

    /**
     * Reset the password of the user.
     * @param userEmail the email of the user to reset the password
     * @return the response
     */
    @PostMapping("/sendMailRecoveryPassword") //TODO: sistemare invio email
    public ResponseEntity<?> sendMailRecoveryPassword(@RequestParam("email") String userEmail) {
        // Find the user with the given email.
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new BadRequestException("User not found with email : " + userEmail));

        // Return the response (200 - OK) and call the method to send the email to recover password.
        return ResponseEntity.ok(customUserDetailsService.sendMailRecoveryPassword(user));
    }


    /**
     * Retrieve the authentication of the user with the given token to request a change of password.
     * @param token the request to reset the password
     * @return the response
     */
    @GetMapping("/getAuthenticationFromEmail") //TODO Da finire
    public ResponseEntity<?> getAuthenticationFromEmail(@RequestParam("token") String token) {
        // Find the password reset token using the given token.
        Optional<PasswordResetToken> userPasswToken = passwordResetTokenRepository.findByToken(token);

        // Check if the token is present.
        if(!userPasswToken.isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(false, "Non è stato trovato nessun token"));
        }

        // Retrieve the user from the token.
        User user = userPasswToken.get().getUser();

        // Request the token to change the password.
        return customUserDetailsService.requestTokenRecoveryPassword(token , user);
    }

    //TODO: metodo recupero password
}
