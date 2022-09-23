package org.rconfalonieri.nzuardi.shootingapp.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.exception.UserException;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.LoginDTO;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.UserDTO;
import org.rconfalonieri.nzuardi.shootingapp.repository.UserRepository;
import org.rconfalonieri.nzuardi.shootingapp.security.helper.UserHelper;
import org.rconfalonieri.nzuardi.shootingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.rconfalonieri.nzuardi.shootingapp.exception.UserException.userExceptionCode.USER_NOT_LOGGED_IN;

@RestController
@RequestMapping("/api")
@Tag(name = "Utente")
public class UserController {
    @Autowired
    private UserHelper userHelper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    /**
     * Dati per il login nel seguente formato
     * {
     * "email" : "mariorossi@gmail.com",
     * "password" : "mario"
     * }
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/authenticate")
    public ResponseEntity<UserHelper.JWTToken> authorize(@Valid @RequestBody LoginDTO loginDto) {
       User u =  userRepository.findById(1l).get();

        return userHelper.authorize(loginDto);
    }

    /**
     * Dati per register nel seguente formato
     * {
     * "email" : "mariorossi@gmail.com",
     * "password" : "ciao1234",
     * "firstName" : "mario",
     * "lastName" : "rossi",
     * "region" : 3
     * }
     */
    @PostMapping("/register/user")
    public User registerUser(@Valid @RequestBody UserDTO userDTO) {
        return userHelper.registerUser(userDTO);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUtente(@PathVariable("id") Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {

            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Dati per register nel seguente formato
     * {
     * "email" : "mariorossi@gmail.com",
     * "password" : "mario",
     * "firstName" : "mario",
     * "lastName" : "rossi",
     * <p>
     * }
     */
    @PostMapping("/register/admin")
    public User registerAdmin(@Valid @RequestBody UserDTO userDTO) {
        setUser(userDTO);

        return userHelper.registerAdmin(userDTO);
    }

    private void setUser(UserDTO userDTO) {
        Optional<User> userLogged = userService.getUserWithAuthorities();
        if (userLogged.isPresent())
            //userDTO.callUser = userLogged.get();
            return;
        else
            throw new UserException(USER_NOT_LOGGED_IN);
    }

    // =================================================================================================================

    @GetMapping("/actualUser")
    public ResponseEntity<User> getActualUser() {
        return ResponseEntity.ok(userService.getUserWithAuthorities().get());
    }
    @GetMapping("/user")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    /**
     * @param probe         Dto contenente i dati della palestra da filtrare.
     * @param page          Pagina da visualizzare
     * @param size          Numero di elementi per pagina
     * @param sortField     Campo per ordinamento
     * @param sortDirection Direzione di ordinamento
     * @return La pagina di risultati della ricerca.
     */
    @Operation(summary = "filter", description = "Filtra le palestre")
    @PostMapping("user/filter")
    ResponseEntity<Page<User>> filter(
            @RequestBody(required = false) User probe,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        return userService.filter(probe, page, size, sortField, sortDirection);
    }
}
