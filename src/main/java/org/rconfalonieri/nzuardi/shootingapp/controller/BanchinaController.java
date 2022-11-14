package org.rconfalonieri.nzuardi.shootingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.model.Banchina;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.BanchinaDto;
import org.rconfalonieri.nzuardi.shootingapp.service.BanchinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller per la gestione delle banchine.
 * Permette di eseguire tutte le operazioni CRUD sulle banchine.
 */
@RequestMapping("/api/banchina")
@RestController
@Tag(name = "Banchina")
public class BanchinaController {

    @Autowired
    BanchinaService banchinaService;

    /**
     * @return Tutti gli abbonamenti presenti nel database.
     */
    @Operation(summary = "findAll", description = "Ottieni tutti gli abbonamenti")
    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(banchinaService.findAll());
    }

    /**
     * @param id Identificativo dell'banchina da ottenere.
     * @return L'banchina con l'identificativo specificato.
     */
    @Operation(summary = "findById", description = "Ritorna banchina con identificativo passato")
    @GetMapping("/{id}")
    public ResponseEntity<Banchina> findById(@PathVariable("id") Long id) {
        Banchina banchina = banchinaService.findById(id);
        return ResponseEntity.ok(banchina);
    }

    /**
     * @param banchinaDto Dto contenente i dati dell'banchina da inserire.
     * @return L'banchina inserito.
     */
    @Operation(summary = "save", description = "Crea un nuovo banchina")
    @PostMapping
    public ResponseEntity<Banchina> save(@RequestBody @Validated BanchinaDto banchinaDto) {
        return ResponseEntity.ok(banchinaService.save(banchinaDto));
    }

    /**
     * @param id Identificativo dell'banchina da eliminare.
     */
    @Operation(summary = "delete", description = "Elimina un banchina")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Optional.ofNullable(banchinaService.findById(id)).orElseThrow(() -> {
            return new ResourceNotFoundException();
        });
        banchinaService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @param banchinaDto Dto contenente i dati dell'banchina da aggiornare.
     * @param id             Identificativo dell'banchina da aggiornare.
     * @return L'banchina aggiornato.
     */
    @Operation(summary = "update", description = "Aggiorna un banchina")
    @PutMapping("/{id}")
    public ResponseEntity<Banchina> update(@RequestBody BanchinaDto banchinaDto, @PathVariable("id") Long id) {
        return ResponseEntity.ok(banchinaService.update(banchinaDto, id));
    }

    /**
     * @param probe         Dto contenente i dati dell'banchina da filtrare.
     * @param page          Pagina dei risultati.
     * @param size          Numero di risultati per pagina.
     * @param sortField     Campo per ordinare i risultati.
     * @param sortDirection Direzione di ordinamento.
     * @return Lista di banchine filtrate.
     */
    @Operation(summary = "filter", description = "Filtra gli abbonamenti")
    @PostMapping("/filter")
    ResponseEntity<Page<Banchina>> filter(
            @RequestBody(required = false) Banchina probe,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        return banchinaService.filter(probe, page, size, sortField, sortDirection);
    }

}