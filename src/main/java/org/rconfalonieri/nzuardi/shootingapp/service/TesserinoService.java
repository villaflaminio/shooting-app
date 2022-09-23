package org.rconfalonieri.nzuardi.shootingapp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rconfalonieri.nzuardi.shootingapp.model.Tesserino;
import org.rconfalonieri.nzuardi.shootingapp.model.PostazioniTiro;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.TesserinoDto;
import org.rconfalonieri.nzuardi.shootingapp.repository.TesserinoRepository;
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
 * Service per gestione tesserino
 */
@Slf4j
@Service
@Transactional
public class TesserinoService {
    @Autowired
    TesserinoRepository tesserinoRepository;

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
     * @param tesserinoDto tesserino da salvare
     * @return tesserino salvato
     */
    public Tesserino save(TesserinoDto tesserinoDto) {
        try {
            // Controllo se la tesserino esiste esiste già
            Tesserino tesserino = new Tesserino();

            // Copio i dati
            BeanUtils.copyProperties(tesserinoDto, tesserino);

            tesserino.setId(null);

            // Salvo l'postazioniTiro
            return tesserinoRepository.save(tesserino);

        } catch (Exception e) {
            log.error("errore salvataggio tesserino", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id identificativo dell'tesserino da eliminare
     */
    public void deleteById(Long id) {
        tesserinoRepository.deleteById(id);
    }

    /**
     * @param id identificativo dell'tesserino da cercare
     * @return tesserino cercato
     */
    public Tesserino findById(Long id) {
        // Controllo se l'tesserino esiste e lo restituisco, altrimenti lancio eccezione di not found
        return tesserinoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * @return lista di tutti gli abbonamenti
     */
    public List<Tesserino> findAll() {
        return tesserinoRepository.findAll();
    }

    /**
     * @param tesserinoDto tesserino modificato
     * @param id             identificativo dell'tesserino da modificare
     * @return tesserino modificato
     */
    public Tesserino update(TesserinoDto tesserinoDto, Long id) {
        // Ottengo tesserino da modificare
        Optional<Tesserino> tesserinoOld = tesserinoRepository.findById(id);
        tesserinoDto.setId(id);

        // Controllo se l'tesserino esiste
        if (tesserinoOld.isPresent()) {
            // Copio i dati
            copyNonNullProperties(tesserinoDto, tesserinoOld.get());
            tesserinoDto.setId(id);

            // Salvo l'tesserino
            return tesserinoRepository.save(tesserinoOld.get());
        } else {
            // Se non esiste lancio eccezione di not found
            throw new ResourceNotFoundException();
        }
    }

    /**
     * @param probe         tesserino utilizzato per filtrare
     * @param page          pagina da visualizzare
     * @param size          numero di elementi per pagina
     * @param sortField     campo per ordinamento
     * @param sortDirection direzione di ordinamento
     * @return lista di tesserini filtrati
     */
    public ResponseEntity<Page<Tesserino>> filter(Tesserino probe, Integer page, Integer size, String sortField, String sortDirection) {
        Pageable pageable;

        // Controllo se l'tesserino da filtrare è nullo.
        if (probe == null) {
            probe = new Tesserino(); // Se è nullo creo un nuovo tesserino
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
        Example<Tesserino> example = Example.of(probe, matcher);

        return ResponseEntity.ok(tesserinoRepository.findAll(example, pageable));
    }
}