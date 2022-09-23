package org.rconfalonieri.nzuardi.shootingapp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rconfalonieri.nzuardi.shootingapp.model.Servizio;
import org.rconfalonieri.nzuardi.shootingapp.model.PostazioniTiro;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ServizioDto;
import org.rconfalonieri.nzuardi.shootingapp.repository.ServizioRepository;
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
 * Service per gestione servizio
 */
@Slf4j
@Service
@Transactional
public class ServizioService {
    @Autowired
    ServizioRepository servizioRepository;

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
     * @param servizioDto servizio da salvare
     * @return servizio salvato
     */
    public Servizio save(ServizioDto servizioDto) {
        try {
            // Controllo se la servizio esiste esiste già
            Servizio servizio = new Servizio();

            // Copio i dati
            BeanUtils.copyProperties(servizioDto, servizio);

            servizio.setId(null);

            // Salvo l'postazioniTiro
            return servizioRepository.save(servizio);

        } catch (Exception e) {
            log.error("errore salvataggio servizio", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id identificativo dell'servizio da eliminare
     */
    public void deleteById(Long id) {
        servizioRepository.deleteById(id);
    }

    /**
     * @param id identificativo dell'servizio da cercare
     * @return servizio cercato
     */
    public Servizio findById(Long id) {
        // Controllo se l'servizio esiste e lo restituisco, altrimenti lancio eccezione di not found
        return servizioRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * @return lista di tutti gli abbonamenti
     */
    public List<Servizio> findAll() {
        return servizioRepository.findAll();
    }

    /**
     * @param servizioDto servizio modificato
     * @param id             identificativo dell'servizio da modificare
     * @return servizio modificato
     */
    public Servizio update(ServizioDto servizioDto, Long id) {
        // Ottengo servizio da modificare
        Optional<Servizio> servizioOld = servizioRepository.findById(id);
        servizioDto.setId(id);

        // Controllo se l'servizio esiste
        if (servizioOld.isPresent()) {
            // Copio i dati
            copyNonNullProperties(servizioDto, servizioOld.get());
            servizioDto.setId(id);

            // Salvo l'servizio
            return servizioRepository.save(servizioOld.get());
        } else {
            // Se non esiste lancio eccezione di not found
            throw new ResourceNotFoundException();
        }
    }

    /**
     * @param probe         servizio utilizzato per filtrare
     * @param page          pagina da visualizzare
     * @param size          numero di elementi per pagina
     * @param sortField     campo per ordinamento
     * @param sortDirection direzione di ordinamento
     * @return lista di servizi filtrati
     */
    public ResponseEntity<Page<Servizio>> filter(Servizio probe, Integer page, Integer size, String sortField, String sortDirection) {
        Pageable pageable;

        // Controllo se l'servizio da filtrare è nullo.
        if (probe == null) {
            probe = new Servizio(); // Se è nullo creo un nuovo servizio
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
        Example<Servizio> example = Example.of(probe, matcher);

        return ResponseEntity.ok(servizioRepository.findAll(example, pageable));
    }
}