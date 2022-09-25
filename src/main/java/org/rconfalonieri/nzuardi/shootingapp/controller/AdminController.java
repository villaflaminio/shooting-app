package org.rconfalonieri.nzuardi.shootingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
import org.rconfalonieri.nzuardi.shootingapp.repository.UserRepository;
import org.rconfalonieri.nzuardi.shootingapp.security.helper.UserHelper;
import org.rconfalonieri.nzuardi.shootingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/")
@Tag(name = "Admin")
public class AdminController {
    @Autowired
    private UserHelper userHelper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/{id}")
    public ResponseEntity<?> getUtente(@PathVariable("id") Long id) {
     User u = userRepository.findById(id).get();
      return ResponseEntity.ok("ok");
    }


    /**
     * Update the user password
     * @param userPrincipal the current user
     * @param newPassword the new password
     * @return the updated user
     */
//    @PostMapping("/changePassword")
//    public User changePassword(@CurrentUser UserPrincipal userPrincipal , @RequestBody String newPassword ){
//        // Find the current user by id.
//        User user = userRepository.findById(userPrincipal.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
//
//        // Update the password.
//        user.setPassword(newPassword);
//
//        // Encode the new password.
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        // Save the user.
//        return userRepository.save(user);
//    }
    @GetMapping("/actualUser") //TODO: controllare se funziona
    public ResponseEntity<User> getActualUser() {
        return ResponseEntity.ok(userService.getUserWithAuthorities().get());
    }
    @GetMapping("/user")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    /**
     * @param probe         Dto contenente i dati degli utenti da filtrare.
     * @param page          Pagina da visualizzare
     * @param size          Numero di elementi per pagina
     * @param sortField     Campo per ordinamento
     * @param sortDirection Direzione di ordinamento
     * @return La pagina di risultati della ricerca.
     */
    @Operation(summary = "filter", description = "Filtra gli utenti")
    @PostMapping("user/filter")
    ResponseEntity<Page<User>> filter(
            @RequestBody(required = false) User probe,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        return userService.filter(probe, page, size, sortField, sortDirection);
    }

    //TODO disattivazione utenti

    //todo prendere tutte le prenotazioni passate di un utente

    //todo mostrare i vecchi tesserini di un utente

    //todo conferma prenotazione, se bisogna pagare dei servizi extra gestire e far pagare in struttura

    //todo dare un nuovo feedback all'istruttore di una prenotazione

}