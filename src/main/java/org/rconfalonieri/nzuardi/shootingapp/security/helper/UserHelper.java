package org.rconfalonieri.nzuardi.shootingapp.security.helper;
import com.google.common.base.Preconditions;
import net.glxn.qrgen.javase.QRCode;
import org.rconfalonieri.nzuardi.shootingapp.exception.UserException;
import org.rconfalonieri.nzuardi.shootingapp.model.Authority;
import org.rconfalonieri.nzuardi.shootingapp.model.Tesserino;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.LoginDTO;
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
import java.util.*;

import static org.rconfalonieri.nzuardi.shootingapp.exception.UserException.userExceptionCode.*;


@Component
public class UserHelper {
    private final TokenProvider tokenProvider;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthorityRepository authorityRepository;
    @Autowired
    private PasswordEncoder bcryptEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    TesserinoRepository tesserinoRepository;
    public UserHelper(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    //TODO login con email per admin e istruttori
    public ResponseEntity<?> authorize(@Valid @RequestBody LoginDTO loginDto) {
        User user = userRepository.findByActualTesserinoId(loginDto.idTesserino).orElseThrow(() -> new UserException(IDTESSERINO_NOT_EXIST));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.idTesserino, loginDto.password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //SecurityContextHolder è una classe di supporto, che forniscono l'accesso al contesto di protezione
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean rememberMe = loginDto.rememberMe != null && loginDto.isRememberMe();



        return tokenProvider.createAuthResponse(authentication,user, rememberMe);
    }

    public void ceckUser(UserDTO userDTO) {
        Preconditions.checkArgument(Objects.nonNull(userDTO.password));
        Preconditions.checkArgument(Objects.nonNull(userDTO.role));
        Preconditions.checkArgument(Objects.nonNull(userDTO.nome));
        Preconditions.checkArgument(Objects.nonNull(userDTO.cognome));

    }

    public User registerUser(UserDTO userDTO) {
        userDTO.role = "USER";
        return register(userDTO);

    }

    public User registerSecretary(UserDTO userDTO) {
        userDTO.role = "SECRETARY";
        return register(userDTO);

    }

    public User registerAdminSecretary(UserDTO userDTO) {
        userDTO.role = "ADMIN_SECRETARY";
        return register(userDTO);

    }

    public User registerAdmin(UserDTO userDTO) {
        userDTO.role = "ADMIN";
        return register(userDTO);
    }

    private User register(UserDTO userDTO)  {
        ceckUser(userDTO);
        if (userRepository.existsByEmail(userDTO.email) || role(userDTO).isEmpty()) {
            throw new UserException(USER_ALREADY_EXISTS);
        }
        User utente = User.builder()
                .password(bcryptEncoder.encode(userDTO.password))
                .nome(userDTO.nome)
                .email(userDTO.email)
                .cognome(userDTO.cognome)
                .sospeso(false)
                .authorities(role(userDTO))
                .build();

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
        userRepository.save(utente);
        tesserinoRepository.save(tesserino);

      //  customUserDetailsService.sendMailPostRegistrazione(utente); //todo riabilitare dopo modifica credenziali gmail

        Optional<User> user = userRepository.findById(utente.getId());
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    private Set<Authority> role(UserDTO userDTO) {
        Set<Authority> author = new HashSet<>();
        switch (userDTO.role) {
            case "USER":
                author.add(authorityRepository.getByName("ROLE_USER"));
                break;

            case "ADMIN":
                author.add(authorityRepository.getByName("ROLE_USER"));
                author.add(authorityRepository.getByName("ROLE_ADMIN"));
                break;
            default:
                throw new UserException(AUTHORITY_NOT_EXIST);
        }
        return author;
    }


}
