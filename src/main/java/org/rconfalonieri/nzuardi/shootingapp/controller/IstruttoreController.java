package org.rconfalonieri.nzuardi.shootingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.model.Istruttore;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.IstruttoreDto;
import org.rconfalonieri.nzuardi.shootingapp.service.IstruttoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller per la gestione degli istruttori.
 * Permette di eseguire tutte le operazioni CRUD per quanto riguarda gli istruttori.
 */
@RequestMapping("/api/istruttore")
@RestController
@Tag(name = "Istruttore")
public class IstruttoreController {

    @Autowired
    IstruttoreService istruttoreService;

    /**
     * @return Tutti gli abbonamenti presenti nel database.
     */
    @Operation(summary = "findAll", description = "Ottieni tutti gli abbonamenti")
    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(istruttoreService.findAll());
    }

    /**
     * @param id Identificativo dell'istruttore da ottenere.
     * @return L'istruttore con l'identificativo specificato.
     */
    @Operation(summary = "findById", description = "Ritorna istruttore con identificativo passato")
    @GetMapping("/{id}")
    public ResponseEntity<Istruttore> findById(@PathVariable("id") Long id) {
        Istruttore istruttore = istruttoreService.findById(id);
        return ResponseEntity.ok(istruttore);
    }

    /**
     * @param istruttoreDto Dto contenente i dati dell'istruttore da inserire.
     * @return L'istruttore inserito.
     */
    @Operation(summary = "save", description = "Crea un nuovo istruttore")
    @PostMapping
    public ResponseEntity<Istruttore> save(@RequestBody @Validated IstruttoreDto istruttoreDto) {
        return ResponseEntity.ok(istruttoreService.save(istruttoreDto));
    }

    /**
     * @param id Identificativo dell'istruttore da eliminare.
     */
    @Operation(summary = "delete", description = "Elimina un istruttore")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Optional.ofNullable(istruttoreService.findById(id)).orElseThrow(() -> {
            return new ResourceNotFoundException();
        });
        istruttoreService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @param istruttoreDto Dto contenente i dati dell'istruttore da aggiornare.
     * @param id             Identificativo dell'istruttore da aggiornare.
     * @return L'istruttore aggiornato.
     */
    @Operation(summary = "update", description = "Aggiorna un istruttore")
    @PutMapping("/{id}")
    public ResponseEntity<Istruttore> update(@RequestBody IstruttoreDto istruttoreDto, @PathVariable("id") Long id) {
        return ResponseEntity.ok(istruttoreService.update(istruttoreDto, id));
    }

    /**
     * @param probe         Dto contenente i dati dell'istruttore da filtrare.
     * @param page          Pagina dei risultati.
     * @param size          Numero di risultati per pagina.
     * @param sortField     Campo per ordinare i risultati.
     * @param sortDirection Direzione di ordinamento.
     * @return Lista di istruttori filtrati.
     */
    @Operation(summary = "filter", description = "Filtra gli abbonamenti")
    @PostMapping("/filter")
    ResponseEntity<Page<Istruttore>> filter(
            @RequestBody(required = false) Istruttore probe,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        return istruttoreService.filter(probe, page, size, sortField, sortDirection);
    }
}