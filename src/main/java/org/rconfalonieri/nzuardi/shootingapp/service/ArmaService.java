package org.rconfalonieri.nzuardi.shootingapp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rconfalonieri.nzuardi.shootingapp.model.Arma;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ArmaDto;
import org.rconfalonieri.nzuardi.shootingapp.repository.ArmaRepository;
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
 * Service per gestione arma
 */
@Slf4j
@Service
@Transactional
public class ArmaService {
    @Autowired
    ArmaRepository armaRepository;

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
     * @param armaDto arma da salvare
     * @return arma salvato
     */
    public Arma save(ArmaDto armaDto) {
        try {
            // Controllo se il seriale esiste esiste già
            Arma arma = armaRepository.findArmaBySeriale(armaDto.getSeriale());

            if (arma == null){
                arma = new Arma();

                // Copio i dati
                BeanUtils.copyProperties(armaDto, arma);

                arma.setId(null);

                // Salvo l'arma
                return armaRepository.save(arma);
            }else{
                log.error("Arma con seriale già esistente");
                throw new Exception("Arma con seriale già esistente");
            }

        } catch (Exception e) {
            log.error("errore salvataggio arma", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id identificativo dell'arma da eliminare
     */
    public void deleteById(Long id) {
        armaRepository.deleteById(id);
    }

    /**
     * @param id identificativo dell'arma da cercare
     * @return arma cercato
     */
    public Arma findById(Long id) {
        // Controllo se l'arma esiste e lo restituisco, altrimenti lancio eccezione di not found
        return armaRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * @return lista di tutti gli abbonamenti
     */
    public List<Arma> findAll() {
        return armaRepository.findAll();
    }

    /**
     * @param armaDto arma modificato
     * @param id             identificativo dell'arma da modificare
     * @return arma modificato
     */
    public Arma update(ArmaDto armaDto, Long id) {
        // Ottengo arma da modificare
        Optional<Arma> armaOld = armaRepository.findById(id);
        armaDto.setId(id);

        // Controllo se l'arma esiste
        if (armaOld.isPresent()) {
            // Copio i dati
            copyNonNullProperties(armaDto, armaOld.get());
            armaDto.setId(id);

            // Salvo l'arma
            return armaRepository.save(armaOld.get());
        } else {
            // Se non esiste lancio eccezione di not found
            throw new ResourceNotFoundException();
        }
    }

    /**
     * @param probe         arma utilizzato per filtrare
     * @param page          pagina da visualizzare
     * @param size          numero di elementi per pagina
     * @param sortField     campo per ordinamento
     * @param sortDirection direzione di ordinamento
     * @return lista di abbonamenti filtrati
     */
    public ResponseEntity<Page<Arma>> filter(Arma probe, Integer page, Integer size, String sortField, String sortDirection) {
        Pageable pageable;

        // Controllo se l'arma da filtrare è nullo.
        if (probe == null) {
            probe = new Arma(); // Se è nullo creo un nuovo arma
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
        Example<Arma> example = Example.of(probe, matcher);

        return ResponseEntity.ok(armaRepository.findAll(example, pageable));
    }

    public Arma setDisponibilita(String seriale, boolean disponibilita) {
        //disabilita l'arma dato il seriale
        Arma arma = armaRepository.findArmaBySeriale(seriale);
        if (arma != null){
            arma.setDisponibile(disponibilita);
            return armaRepository.save(arma);
        }else{
            log.error("Arma non trovata");
            throw new ResourceNotFoundException("Arma non trovata");
        }
    }
}