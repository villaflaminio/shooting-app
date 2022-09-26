package org.rconfalonieri.nzuardi.shootingapp.service;

import org.apache.commons.lang3.StringUtils;
import org.rconfalonieri.nzuardi.shootingapp.model.Prenotazione;
import org.rconfalonieri.nzuardi.shootingapp.model.Tesserino;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
import org.rconfalonieri.nzuardi.shootingapp.repository.UserRepository;
import org.rconfalonieri.nzuardi.shootingapp.security.SecurityUtils;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByEmail);
    }

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }
    /**
     * @param probe         Dto contenente i dati della palestra da filtrare.
     * @param page          Pagina da visualizzare
     * @param size          Numero di elementi per pagina
     * @param sortField     Campo per ordinamento
     * @param sortDirection Direzione di ordinamento
     * @return La pagina di risultati della ricerca.
     */
    public ResponseEntity<Page<User>> filter(User probe, Integer page, Integer size, String sortField, String sortDirection) {
        Pageable pageable;

        // Controllo se la palestra per filtrare è nulla
        if (probe == null) {
            probe = new User();
        }
        probe.setAuthorities(null);
        // Controllo se il campo per ordinare è nullo
        if (StringUtils.isEmpty(sortField)) {
            pageable = PageRequest.of(page, size); // Se è nullo, ordino per id
        } else {
            // Se non è nullo, ordino per il campo specificato
            Sort.Direction dir = StringUtils.isEmpty(sortDirection) ? Sort.Direction.ASC : Sort.Direction.valueOf(sortDirection.trim().toUpperCase());
            pageable = PageRequest.of(page, size, dir, sortField);
        }

        // Filtro le palestre
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreCase().withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.STARTING);
        Example<User> example = Example.of(probe, matcher);

        return ResponseEntity.ok(userRepository.findAll(example, pageable));
    }

    public ResponseEntity<?> setStateUser(Long id, boolean enable) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setSospeso(enable);
            userRepository.save(user.get());
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<List<Prenotazione>> getOldPrenotazioni() {
        Optional<User> user = SecurityUtils.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByEmail);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get().getPrenotazioni());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    public ResponseEntity<List<Tesserino>> getOldTesserini() {
        Optional<User> user = SecurityUtils.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByEmail);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get().getTesserini());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    public ResponseEntity<List<Prenotazione>> getTodayPrenotazioni() {
//
//
//    }
}
