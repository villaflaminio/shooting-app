package org.rconfalonieri.nzuardi.shootingapp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rconfalonieri.nzuardi.shootingapp.model.Prenotazione;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.PrenotazioneDto;
import org.rconfalonieri.nzuardi.shootingapp.repository.PrenotazioneRepository;
import org.rconfalonieri.nzuardi.shootingapp.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service per gestione prenotazione
 */
@Slf4j
@Service
@Transactional
public class PrenotazioneService {
    @Autowired
    PrenotazioneRepository prenotazioneRepository;

    @Autowired
    UserRepository userRepository;

    // ===========================================================================
    public static void copyNonNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    // ===========================================================================

    /**
     * @param prenotazioneDto prenotazione da salvare
     * @return prenotazione salvato
     */
    public Prenotazione save(PrenotazioneDto prenotazioneDto) {
        try {
            // Controllo se la prenotazione esiste esiste già
            Prenotazione prenotazione = new Prenotazione();

            // Copio i dati
            BeanUtils.copyProperties(prenotazioneDto, prenotazione);

            prenotazione.setId(null);

            // Salvo l'postazioniTiro
            return prenotazioneRepository.save(prenotazione);

        } catch (Exception e) {
            log.error("errore salvataggio prenotazione", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id identificativo dell'prenotazione da eliminare
     */
    public void deleteById(Long id) {
        prenotazioneRepository.deleteById(id);
    }

    /**
     * @param id identificativo dell'prenotazione da cercare
     * @return prenotazione cercato
     */
    public Prenotazione findById(Long id) {
        // Controllo se l'prenotazione esiste e lo restituisco, altrimenti lancio eccezione di not found
        return prenotazioneRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * @return lista di tutti gli abbonamenti
     */
    public List<Prenotazione> findAll() {
        return prenotazioneRepository.findAll();
    }

    /**
     * @param prenotazioneDto prenotazione modificato
     * @param id             identificativo dell'prenotazione da modificare
     * @return prenotazione modificato
     */
    public Prenotazione update(PrenotazioneDto prenotazioneDto, Long id) {
        // Ottengo prenotazione da modificare
        Optional<Prenotazione> prenotazioneOld = prenotazioneRepository.findById(id);
        prenotazioneDto.setId(id);

        // Controllo se l'prenotazione esiste
        if (prenotazioneOld.isPresent()) {
            // Copio i dati
            copyNonNullProperties(prenotazioneDto, prenotazioneOld.get());
            prenotazioneDto.setId(id);

            // Salvo l'prenotazione
            return prenotazioneRepository.save(prenotazioneOld.get());
        } else {
            // Se non esiste lancio eccezione di not found
            throw new ResourceNotFoundException();
        }
    }

    /**
     * @param probe         prenotazione utilizzato per filtrare
     * @param page          pagina da visualizzare
     * @param size          numero di elementi per pagina
     * @param sortField     campo per ordinamento
     * @param sortDirection direzione di ordinamento
     * @return lista di prenotazioni filtrate
     */
    public ResponseEntity<Page<Prenotazione>> filter(Prenotazione probe, Integer page, Integer size, String sortField, String sortDirection) {
        Pageable pageable;

        // Controllo se l'prenotazione da filtrare è nullo.
        if (probe == null) {
            probe = new Prenotazione(); // Se è nullo creo un nuovo prenotazione
        }

        // Controllo se il campo di ordinamento è nullo.
        if (StringUtils.isEmpty(sortField)) {
            pageable = PageRequest.of(page, size); // Se è nullo ordino per id
        } else {
            // Se non è nullo ordino per il campo di ordinamento
            Sort.Direction dir = StringUtils.isEmpty(sortDirection) ? Sort.Direction.ASC : Sort.Direction.valueOf(sortDirection.trim().toUpperCase());
            pageable = PageRequest.of(page, size, dir, sortField);
        }

        // Filtro gli abbonamenti
        ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreCase().withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.STARTING);
        Example<Prenotazione> example = Example.of(probe, matcher);

        return ResponseEntity.ok(prenotazioneRepository.findAll(example, pageable));
    }
}