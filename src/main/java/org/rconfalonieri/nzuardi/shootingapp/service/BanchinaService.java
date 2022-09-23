package org.rconfalonieri.nzuardi.shootingapp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rconfalonieri.nzuardi.shootingapp.model.Banchina;
import org.rconfalonieri.nzuardi.shootingapp.model.PostazioniTiro;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.BanchinaDto;
import org.rconfalonieri.nzuardi.shootingapp.repository.BanchinaRepository;
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
 * Service per gestione banchina
 */
@Slf4j
@Service
@Transactional
public class BanchinaService {
    @Autowired
    BanchinaRepository banchinaRepository;

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
     * @param banchinaDto banchina da salvare
     * @return banchina salvato
     */
    public Banchina save(BanchinaDto banchinaDto) {
        try {
            // Controllo se la banchina esiste esiste già
            Banchina banchina = new Banchina();

            // Copio i dati
            BeanUtils.copyProperties(banchinaDto, banchina);

            banchina.setId(null);

            // Salvo l'postazioniTiro
            return banchinaRepository.save(banchina);

        } catch (Exception e) {
            log.error("errore salvataggio banchina", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id identificativo dell'banchina da eliminare
     */
    public void deleteById(Long id) {
        banchinaRepository.deleteById(id);
    }

    /**
     * @param id identificativo dell'banchina da cercare
     * @return banchina cercato
     */
    public Banchina findById(Long id) {
        // Controllo se l'banchina esiste e lo restituisco, altrimenti lancio eccezione di not found
        return banchinaRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * @return lista di tutti gli abbonamenti
     */
    public List<Banchina> findAll() {
        return banchinaRepository.findAll();
    }

    /**
     * @param banchinaDto banchina modificato
     * @param id             identificativo dell'banchina da modificare
     * @return banchina modificato
     */
    public Banchina update(BanchinaDto banchinaDto, Long id) {
        // Ottengo banchina da modificare
        Optional<Banchina> banchinaOld = banchinaRepository.findById(id);
        banchinaDto.setId(id);

        // Controllo se l'banchina esiste
        if (banchinaOld.isPresent()) {
            // Copio i dati
            copyNonNullProperties(banchinaDto, banchinaOld.get());
            banchinaDto.setId(id);

            // Salvo l'banchina
            return banchinaRepository.save(banchinaOld.get());
        } else {
            // Se non esiste lancio eccezione di not found
            throw new ResourceNotFoundException();
        }
    }

    /**
     * @param probe         banchina utilizzato per filtrare
     * @param page          pagina da visualizzare
     * @param size          numero di elementi per pagina
     * @param sortField     campo per ordinamento
     * @param sortDirection direzione di ordinamento
     * @return lista di abbonamenti filtrati
     */
    public ResponseEntity<Page<Banchina>> filter(Banchina probe, Integer page, Integer size, String sortField, String sortDirection) {
        Pageable pageable;

        // Controllo se l'banchina da filtrare è nullo.
        if (probe == null) {
            probe = new Banchina(); // Se è nullo creo un nuovo banchina
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
        Example<Banchina> example = Example.of(probe, matcher);

        return ResponseEntity.ok(banchinaRepository.findAll(example, pageable));
    }
}