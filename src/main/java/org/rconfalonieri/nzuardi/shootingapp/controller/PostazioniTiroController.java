package org.rconfalonieri.nzuardi.shootingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.model.Arma;
import org.rconfalonieri.nzuardi.shootingapp.model.PostazioniTiro;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.PostazioniTiroDto;
import org.rconfalonieri.nzuardi.shootingapp.service.PostazioniTiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller per la gestione delle postazioni di tiro.
 * Permette di eseguire tutte le operazioni CRUD sulle postazioni di tiro.
 */
@RequestMapping("/api/postazioniTiro")
@RestController
@Tag(name = "PostazioniTiro")
public class PostazioniTiroController {

    @Autowired
    PostazioniTiroService postazioniTiroService;

    /**
     * @return Tutti gli abbonamenti presenti nel database.
     */
    @Operation(summary = "findAll", description = "Ottieni tutti gli abbonamenti")
    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(postazioniTiroService.findAll());
    }

    /**
     * @param id Identificativo dell'postazioneTiro da ottenere.
     * @return L'postazioneTiro con l'identificativo specificato.
     */
    @Operation(summary = "findById", description = "Ritorna postazioneTiro con identificativo passato")
    @GetMapping("/{id}")
    public ResponseEntity<PostazioniTiro> findById(@PathVariable("id") Long id) {
        PostazioniTiro postazioneTiro = postazioniTiroService.findById(id);
        return ResponseEntity.ok(postazioneTiro);
    }

    /**
     * @param postazioneTiroDto Dto contenente i dati dell'postazioneTiro da inserire.
     * @return L'postazioneTiro inserito.
     */
    @Operation(summary = "save", description = "Crea un nuovo postazioneTiro")
    @PostMapping
    public ResponseEntity<PostazioniTiro> save(@RequestBody @Validated PostazioniTiroDto postazioneTiroDto) {
        return ResponseEntity.ok(postazioniTiroService.save(postazioneTiroDto));
    }

    /**
     * @param id Identificativo dell'postazioneTiro da eliminare.
     */
    @Operation(summary = "delete", description = "Elimina un postazioneTiro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Optional.ofNullable(postazioniTiroService.findById(id)).orElseThrow(() -> {
            return new ResourceNotFoundException();
        });
        postazioniTiroService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @param postazioneTiroDto Dto contenente i dati dell'postazioneTiro da aggiornare.
     * @param id             Identificativo dell'postazioneTiro da aggiornare.
     * @return L'postazioneTiro aggiornato.
     */
    @Operation(summary = "update", description = "Aggiorna un postazioneTiro")
    @PutMapping("/{id}")
    public ResponseEntity<PostazioniTiro> update(@RequestBody PostazioniTiroDto postazioneTiroDto, @PathVariable("id") Long id) {
        return ResponseEntity.ok(postazioniTiroService.update(postazioneTiroDto, id));
    }

    /**
     * @param probe         Dto contenente i dati dell'postazioneTiro da filtrare.
     * @param page          Pagina dei risultati.
     * @param size          Numero di risultati per pagina.
     * @param sortField     Campo per ordinare i risultati.
     * @param sortDirection Direzione di ordinamento.
     * @return Lista di postazioni di tiro filtrate.
     */
    @Operation(summary = "filter", description = "Filtra le postazioni di tiro")
    @PostMapping("/filter")
    ResponseEntity<Page<PostazioniTiro>> filter(
            @RequestBody(required = false) PostazioniTiro probe,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        return postazioniTiroService.filter(probe, page, size, sortField, sortDirection);
    }

    //todo api per disattivare la postazione di tiro

    @Operation(summary = "setAttiva", description = "Disabilita una postazione di tiro")
    @PutMapping("/setAttiva/{id}")
    public ResponseEntity<PostazioniTiro> disabilita(@PathVariable("id") Long id, @RequestParam Boolean disponibilita) {
        return ResponseEntity.ok(postazioniTiroService.setAttiva(id,disponibilita));
    }

}
