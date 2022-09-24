package org.rconfalonieri.nzuardi.shootingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.model.Arma;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ArmaDto;
import org.rconfalonieri.nzuardi.shootingapp.service.ArmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller per la gestione delle armi.
 * Permette di eseguire tutte le operazioni CRUD sulle armi.
 */
@RequestMapping("/api/arma")
@RestController
@Tag(name = "Arma")
public class ArmaController {

    @Autowired
    ArmaService armaService;

    /**
     * @return Tutti gli abbonamenti presenti nel database.
     */
    @Operation(summary = "findAll", description = "Ottieni tutti gli abbonamenti")
    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(armaService.findAll());
    }

    /**
     * @param id Identificativo dell'arma da ottenere.
     * @return L'arma con l'identificativo specificato.
     */
    @Operation(summary = "findById", description = "Ritorna arma con identificativo passato")
    @GetMapping("/{id}")
    public ResponseEntity<Arma> findById(@PathVariable("id") Long id) {
        Arma arma = armaService.findById(id);
        return ResponseEntity.ok(arma);
    }

    /**
     * @param armaDto Dto contenente i dati dell'arma da inserire.
     * @return L'arma inserito.
     */
    @Operation(summary = "save", description = "Crea un nuovo arma")
    @PostMapping
    public ResponseEntity<Arma> save(@RequestBody @Validated ArmaDto armaDto) {
        return ResponseEntity.ok(armaService.save(armaDto));
    }

    /**
     * @param id Identificativo dell'arma da eliminare.
     */
    @Operation(summary = "delete", description = "Elimina un arma")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Optional.ofNullable(armaService.findById(id)).orElseThrow(() -> {
            return new ResourceNotFoundException();
        });
        armaService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @param armaDto Dto contenente i dati dell'arma da aggiornare.
     * @param id             Identificativo dell'arma da aggiornare.
     * @return L'arma aggiornato.
     */
    @Operation(summary = "update", description = "Aggiorna un arma")
    @PutMapping("/{id}")
    public ResponseEntity<Arma> update(@RequestBody ArmaDto armaDto, @PathVariable("id") Long id) {
        return ResponseEntity.ok(armaService.update(armaDto, id));
    }

    /**
     * @param probe         Dto contenente i dati dell'arma da filtrare.
     * @param page          Pagina dei risultati.
     * @param size          Numero di risultati per pagina.
     * @param sortField     Campo per ordinare i risultati.
     * @param sortDirection Direzione di ordinamento.
     * @return Lista di armi filtrate.
     */
    @Operation(summary = "filter", description = "Filtra gli abbonamenti")
    @PostMapping("/filter")
    ResponseEntity<Page<Arma>> filter(
            @RequestBody(required = false) Arma probe,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        return armaService.filter(probe, page, size, sortField, sortDirection);
    }

    //todo disabilita arma dato il seriale
    //todo abilita arma dato il seriale


}