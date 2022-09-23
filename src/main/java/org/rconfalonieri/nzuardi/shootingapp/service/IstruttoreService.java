package org.rconfalonieri.nzuardi.shootingapp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rconfalonieri.nzuardi.shootingapp.model.Istruttore;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.IstruttoreDto;
import org.rconfalonieri.nzuardi.shootingapp.repository.IstruttoreRepository;
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
 * Service per gestione istruttore
 */
@Slf4j
@Service
@Transactional
public class IstruttoreService {
    @Autowired
    IstruttoreRepository istruttoreRepository;

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
     * @param istruttoreDto istruttore da salvare
     * @return istruttore salvato
     */
    public Istruttore save(IstruttoreDto istruttoreDto) {
        try {
            // Controllo se l'utente esiste
            Optional<Istruttore> user = istruttoreRepository.findById(istruttoreDto.getId());

            if (user.isPresent()){
                log.error("Istruttore con seriale già esistente");
                throw new Exception("Istruttore con seriale già esistente");
            }else{
                Istruttore istruttore = new Istruttore();

                // Copio i dati
                BeanUtils.copyProperties(istruttoreDto, istruttore);

                istruttore.setId(null);

                // Salvo l'istruttore
                return istruttoreRepository.save(istruttore);
            }
        } catch (Exception e) {
            log.error("errore salvataggio istruttore", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id identificativo dell'istruttore da eliminare
     */
    public void deleteById(Long id) {
        istruttoreRepository.deleteById(id);
    }

    /**
     * @param id identificativo dell'istruttore da cercare
     * @return istruttore cercato
     */
    public Istruttore findById(Long id) {
        // Controllo se l'istruttore esiste e lo restituisco, altrimenti lancio eccezione di not found
        return istruttoreRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * @return lista di tutti gli abbonamenti
     */
    public List<Istruttore> findAll() {
        return istruttoreRepository.findAll();
    }

    /**
     * @param istruttoreDto istruttore modificato
     * @param id             identificativo dell'istruttore da modificare
     * @return istruttore modificato
     */
    public Istruttore update(IstruttoreDto istruttoreDto, Long id) {
        // Ottengo istruttore da modificare
        Optional<Istruttore> istruttoreOld = istruttoreRepository.findById(id);
        istruttoreDto.setId(id);

        // Controllo se l'istruttore esiste
        if (istruttoreOld.isPresent()) {
            // Copio i dati
            copyNonNullProperties(istruttoreDto, istruttoreOld.get());
            istruttoreDto.setId(id);

            // Salvo l'istruttore
            return istruttoreRepository.save(istruttoreOld.get());
        } else {
            // Se non esiste lancio eccezione di not found
            throw new ResourceNotFoundException();
        }
    }

    /**
     * @param probe         istruttore utilizzato per filtrare
     * @param page          pagina da visualizzare
     * @param size          numero di elementi per pagina
     * @param sortField     campo per ordinamento
     * @param sortDirection direzione di ordinamento
     * @return lista di abbonamenti filtrati
     */
    public ResponseEntity<Page<Istruttore>> filter(Istruttore probe, Integer page, Integer size, String sortField, String sortDirection) {
        Pageable pageable;

        // Controllo se l'istruttore da filtrare è nullo.
        if (probe == null) {
            probe = new Istruttore(); // Se è nullo creo un nuovo istruttore
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
        Example<Istruttore> example = Example.of(probe, matcher);

        return ResponseEntity.ok(istruttoreRepository.findAll(example, pageable));
    }
}