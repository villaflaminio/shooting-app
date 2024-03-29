package org.rconfalonieri.nzuardi.shootingapp.security.helper;
import com.google.common.base.Preconditions;
import net.glxn.qrgen.javase.QRCode;
import org.rconfalonieri.nzuardi.shootingapp.exception.BadRequestException;
import org.rconfalonieri.nzuardi.shootingapp.exception.ResourceNotFoundException;
import org.rconfalonieri.nzuardi.shootingapp.exception.UserException;
import org.rconfalonieri.nzuardi.shootingapp.model.Authority;
import org.rconfalonieri.nzuardi.shootingapp.model.PasswordResetToken;
import org.rconfalonieri.nzuardi.shootingapp.model.Tesserino;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ApiResponseDto;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.LoginDTO;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.LoginUserDTO;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.UserDTO;
import org.rconfalonieri.nzuardi.shootingapp.repository.AuthorityRepository;
import org.rconfalonieri.nzuardi.shootingapp.repository.PasswordResetTokenRepository;
import org.rconfalonieri.nzuardi.shootingapp.repository.TesserinoRepository;
import org.rconfalonieri.nzuardi.shootingapp.repository.UserRepository;
import org.rconfalonieri.nzuardi.shootingapp.security.CustomUserDetailsService;
import org.rconfalonieri.nzuardi.shootingapp.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.*;

import static org.rconfalonieri.nzuardi.shootingapp.exception.UserException.userExceptionCode.*;


@Component
public class UserHelper {
    private final TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TesserinoRepository tesserinoRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private PasswordEncoder bcryptEncoder;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    public UserHelper(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }


    public ResponseEntity<?> authorizeUser(@Valid @RequestBody LoginUserDTO loginDto) {

        User user = userRepository.findByActualTesserinoId(loginDto.idTesserino).orElseThrow(() -> new ResourceNotFoundException("idTesserino", "idTesserino", loginDto.idTesserino));

        if(user.isSospeso())   return  ResponseEntity.status(401).body(new ApiResponseDto(false, "Utente sospeso"));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.idTesserino, loginDto.password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //SecurityContextHolder è una classe di supporto, che forniscono l'accesso al contesto di protezione
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean rememberMe = loginDto.rememberMe != null && loginDto.isRememberMe();
        return tokenProvider.createAuthResponse(authentication,user, rememberMe);
    }
    public ResponseEntity<?> authorizeUser(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //SecurityContextHolder è una classe di supporto, che forniscono l'accesso al contesto di protezione
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.createAuthResponse(authentication,user, false);
    }

    public ResponseEntity<?> authorize(@Valid @RequestBody LoginDTO loginDto) {
        User user = userRepository.findByEmail(loginDto.email).orElseThrow(() -> new ResourceNotFoundException("email", "email", loginDto.email));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.email, loginDto.password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //SecurityContextHolder è una classe di supporto, che forniscono l'accesso al contesto di protezione
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean rememberMe = loginDto.rememberMe != null && loginDto.isRememberMe();
        return tokenProvider.createAuthResponse(authentication,user, rememberMe);
    }
    public void ceckUser(UserDTO userDTO) {
        Preconditions.checkArgument(Objects.nonNull(userDTO.password));
        Preconditions.checkArgument(Objects.nonNull(userDTO.nome));
        Preconditions.checkArgument(Objects.nonNull(userDTO.cognome));

    }

    public User registerUser(UserDTO userDTO) {

        User user = register(userDTO,getRoles("USER"));
        generateTesserino(user);
        return userRepository.findById(user.getId()).orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
    public boolean registerFirstUser() {
        Set<Authority> author = new HashSet<>();

        UserDTO userDTO = UserDTO.builder()
                .nome("admin")
                .cognome("admin")
                .password("password")
                .email("admin@gmail.com")
                .build();
        author.add(authorityRepository.existsByName("ROLE_USER") ?  authorityRepository.getByName("ROLE_USER") : authorityRepository.save(new Authority("ROLE_USER")) );
        author.add(authorityRepository.existsByName("ROLE_ISTRUTTORE") ? authorityRepository.getByName("ROLE_ISTRUTTORE") : authorityRepository.save(new Authority("ROLE_ISTRUTTORE")));
        author.add(authorityRepository.existsByName("ROLE_ADMIN") ?  authorityRepository.getByName("ROLE_ADMIN") : authorityRepository.save(new Authority("ROLE_ADMIN")));
        register(userDTO,author);
        return true;
    }

    public User registerAdmin(UserDTO userDTO) {
        User user = register(userDTO,getRoles("ADMIN"));
        customUserDetailsService.sendMailPostRegistrazione(user);
        return user;
    }

    public User registerIstruttore(UserDTO userDTO) {
        User user = register(userDTO,getRoles("ISTRUTTORE"));
        customUserDetailsService.sendMailPostRegistrazione(user);
        return user;
    }

    private Tesserino generateTesserino(User utente)  {
        Date dataRilascio = new Date();
        ByteArrayOutputStream stream = QRCode
                .from("Tesserino id: " + utente.getId() + " Nome: " + utente.getNome() + " Cognome: " + utente.getCognome())
                .withSize(250, 250)
                .stream();
        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

        Tesserino tesserino = new Tesserino().builder()
                .dataRilascio(dataRilascio)
                .utente(utente)
                .dataScadenza(new Date(dataRilascio.getTime() + (1000L * 60 * 60 * 24 * 365)))
                .qrCode("data:image/png;base64," + Base64.getEncoder().encodeToString(stream.toByteArray()))
                .build();
          customUserDetailsService.sendMailPostRegistrazione(utente);

        return tesserinoRepository.save(tesserino);

    }

    private User register(UserDTO userDTO,Set<Authority> roles)  {
        ceckUser(userDTO);
        if (userRepository.existsByEmail(userDTO.email)) {
            throw new UserException(USER_ALREADY_EXISTS);
        }
        User utente = User.builder()
                .password(bcryptEncoder.encode(userDTO.password))
                .nome(userDTO.nome)
                .email(userDTO.email)
                .cognome(userDTO.cognome)
                .sospeso(false)
                .authorities(roles)
                .build();

        return userRepository.save(utente);

    }

    private Set<Authority> getRoles(String role) {
        Set<Authority> author = new HashSet<>();
        switch (role) {
            case "USER":
                author.add(authorityRepository.getByName("ROLE_USER"));
                break;
            case "ISTRUTTORE":
                author.add(authorityRepository.getByName("ROLE_ISTRUTTORE"));
                break;
            case "ADMIN":
                author.add(authorityRepository.getByName("ROLE_USER"));
                author.add(authorityRepository.getByName("ROLE_ISTRUTTORE"));
                author.add(authorityRepository.getByName("ROLE_ADMIN"));
                break;
            default:
                throw new UserException(AUTHORITY_NOT_EXIST);
        }
        return author;
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
     * Request token recovery password.
     * @param token
     * @param user
     * @return
     */
    public ResponseEntity<?> requestTokenRecoveryPassword(String token , User user) {
        String result = validatePasswordResetToken(token);
        if(result != null) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(false, result));
        } else {
            return authorizeUser(user);
        }
    }

}
