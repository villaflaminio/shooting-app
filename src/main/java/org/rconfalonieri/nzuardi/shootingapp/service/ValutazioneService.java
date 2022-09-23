package org.rconfalonieri.nzuardi.shootingapp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rconfalonieri.nzuardi.shootingapp.model.Valutazione;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ValutazioneDto;
import org.rconfalonieri.nzuardi.shootingapp.repository.ValutazioneRepository;
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
 * Service per gestione valutazione
 */
@Slf4j
@Service
@Transactional
public class ValutazioneService {
    @Autowired
    ValutazioneRepository valutazioneRepository;

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
     * @param valutazioneDto valutazione da salvare
     * @return valutazione salvato
     */
    public Valutazione save(ValutazioneDto valutazioneDto) {
        try {
            // Controllo se la valutazione esiste esiste già
            Valutazione valutazione = new Valutazione();

            // Copio i dati
            BeanUtils.copyProperties(valutazioneDto, valutazione);

            valutazione.setId(null);

            // Salvo l'postazioniTiro
            return valutazioneRepository.save(valutazione);

        } catch (Exception e) {
            log.error("errore salvataggio valutazione", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id identificativo dell'valutazione da eliminare
     */
    public void deleteById(Long id) {
        valutazioneRepository.deleteById(id);
    }

    /**
     * @param id identificativo dell'valutazione da cercare
     * @return valutazione cercato
     */
    public Valutazione findById(Long id) {
        // Controllo se l'valutazione esiste e lo restituisco, altrimenti lancio eccezione di not found
        return valutazioneRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * @return lista di tutti gli abbonamenti
     */
    public List<Valutazione> findAll() {
        return valutazioneRepository.findAll();
    }

    /**
     * @param valutazioneDto valutazione modificato
     * @param id             identificativo dell'valutazione da modificare
     * @return valutazione modificato
     */
    public Valutazione update(ValutazioneDto valutazioneDto, Long id) {
        // Ottengo valutazione da modificare
        Optional<Valutazione> valutazioneOld = valutazioneRepository.findById(id);
        valutazioneDto.setId(id);

        // Controllo se l'valutazione esiste
        if (valutazioneOld.isPresent()) {
            // Copio i dati
            copyNonNullProperties(valutazioneDto, valutazioneOld.get());
            valutazioneDto.setId(id);

            // Salvo l'valutazione
            return valutazioneRepository.save(valutazioneOld.get());
        } else {
            // Se non esiste lancio eccezione di not found
            throw new ResourceNotFoundException();
        }
    }

    /**
     * @param probe         valutazione utilizzato per filtrare
     * @param page          pagina da visualizzare
     * @param size          numero di elementi per pagina
     * @param sortField     campo per ordinamento
     * @param sortDirection direzione di ordinamento
     * @return lista di valutazioni filtrate
     */
    public ResponseEntity<Page<Valutazione>> filter(Valutazione probe, Integer page, Integer size, String sortField, String sortDirection) {
        Pageable pageable;

        // Controllo se l'valutazione da filtrare è nullo.
        if (probe == null) {
            probe = new Valutazione(); // Se è nullo creo un nuovo valutazione
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
        Example<Valutazione> example = Example.of(probe, matcher);

        return ResponseEntity.ok(valutazioneRepository.findAll(example, pageable));
    }
}