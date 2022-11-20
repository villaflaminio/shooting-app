package org.rconfalonieri.nzuardi.shootingapp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rconfalonieri.nzuardi.shootingapp.controller.BanchinaController;
import org.rconfalonieri.nzuardi.shootingapp.model.Arma;
import org.rconfalonieri.nzuardi.shootingapp.model.Banchina;
import org.rconfalonieri.nzuardi.shootingapp.model.PostazioniTiro;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.PostazioniTiroDto;
import org.rconfalonieri.nzuardi.shootingapp.repository.BanchinaRepository;
import org.rconfalonieri.nzuardi.shootingapp.repository.PostazioniTiroRepository;
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
 * Service per gestione postazioni di tiro
 */
@Slf4j
@Service
@Transactional
public class PostazioniTiroService {
    @Autowired
    PostazioniTiroRepository postazioniTiroRepository;

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
     * @param postazioniTiroDto postazioniTiro da salvare
     * @return postazioniTiro salvato
     */
    public PostazioniTiro save(PostazioniTiroDto postazioniTiroDto) {
        try {
            // Controllo se la postazione esiste esiste già
            PostazioniTiro postazioniTiro = new PostazioniTiro();

            // Copio i dati
            BeanUtils.copyProperties(postazioniTiroDto, postazioniTiro);

            postazioniTiro.setId(null);
            Banchina  banchina = banchinaRepository.findById(postazioniTiroDto.getIdBanchina()).orElseThrow(() -> new ResourceNotFoundException("Banchina"));
            postazioniTiro.setBanchina(banchina);
            // Salvo l'postazioniTiro
            return postazioniTiroRepository.save(postazioniTiro);

        } catch (Exception e) {
            log.error("errore salvataggio postazioniTiro", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id identificativo dell'postazioniTiro da eliminare
     */
    public void deleteById(Long id) {
        postazioniTiroRepository.deleteById(id);
    }

    /**
     * @param id identificativo dell'postazioniTiro da cercare
     * @return postazioniTiro cercato
     */
    public PostazioniTiro findById(Long id) {
        // Controllo se l'postazioniTiro esiste e lo restituisco, altrimenti lancio eccezione di not found
        return postazioniTiroRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * @return lista di tutti gli abbonamenti
     */
    public List<PostazioniTiro> findAll() {
        return postazioniTiroRepository.findAll();
    }

    /**
     * @param postazioniTiroDto postazioniTiro modificato
     * @param id             identificativo dell'postazioniTiro da modificare
     * @return postazioniTiro modificato
     */
    public PostazioniTiro update(PostazioniTiroDto postazioniTiroDto, Long id) {
        // Ottengo postazioniTiro da modificare
        Optional<PostazioniTiro> postazioniTiroOld = postazioniTiroRepository.findById(id);
        postazioniTiroDto.setId(id);

        // Controllo se l'postazioniTiro esiste
        if (postazioniTiroOld.isPresent()) {
            // Copio i dati
            copyNonNullProperties(postazioniTiroDto, postazioniTiroOld.get());
            postazioniTiroDto.setId(id);

            // Salvo l'postazioniTiro
            return postazioniTiroRepository.save(postazioniTiroOld.get());
        } else {
            // Se non esiste lancio eccezione di not found
            throw new ResourceNotFoundException();
        }
    }

    /**
     * @param probe         postazioniTiro utilizzato per filtrare
     * @param page          pagina da visualizzare
     * @param size          numero di elementi per pagina
     * @param sortField     campo per ordinamento
     * @param sortDirection direzione di ordinamento
     * @return lista di abbonamenti filtrati
     */
    public ResponseEntity<Page<PostazioniTiro>> filter(PostazioniTiro probe, Integer page, Integer size, String sortField, String sortDirection) {
        Pageable pageable;

        // Controllo se l'postazioniTiro da filtrare è nullo.
        if (probe == null) {
            probe = new PostazioniTiro(); // Se è nullo creo un nuovo postazioniTiro
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
        Example<PostazioniTiro> example = Example.of(probe, matcher);

        return ResponseEntity.ok(postazioniTiroRepository.findAll(example, pageable));
    }

    public PostazioniTiro setAttiva(Long id, Boolean attiva) {
        //disabilita l'arma dato il seriale
        PostazioniTiro postazioniTiro = postazioniTiroRepository.findPostazioniTiroById(id);
        if (postazioniTiro != null){
            postazioniTiro.setAttiva(attiva);
            return postazioniTiroRepository.save(postazioniTiro);
        }else{
            log.error("postazioniTiro non trovata");
            throw new ResourceNotFoundException("postazioniTiro non trovata");
        }
    }
}