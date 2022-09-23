package org.rconfalonieri.nzuardi.shootingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.model.Servizio;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ServizioDto;
import org.rconfalonieri.nzuardi.shootingapp.service.ServizioService;
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
@RequestMapping("/api/servizio")
@RestController
@Tag(name = "Servizio")
public class ServizioController {

    @Autowired
    ServizioService servizioService;

    /**
     * @return Tutti gli abbonamenti presenti nel database.
     */
    @Operation(summary = "findAll", description = "Ottieni tutti gli abbonamenti")
    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(servizioService.findAll());
    }

    /**
     * @param id Identificativo del servizio da ottenere.
     * @return servizio con  identificativo specificato.
     */
    @Operation(summary = "findById", description = "Ritorna servizio con identificativo passato")
    @GetMapping("/{id}")
    public ResponseEntity<Servizio> findById(@PathVariable("id") Long id) {
        Servizio servizio = servizioService.findById(id);
        return ResponseEntity.ok(servizio);
    }

    /**
     * @param servizioDto Dto contenente i dati del servizio da inserire.
     * @return servizio inserito.
     */
    @Operation(summary = "save", description = "Crea un nuovo servizio")
    @PostMapping
    public ResponseEntity<Servizio> save(@RequestBody @Validated ServizioDto servizioDto) {
        return ResponseEntity.ok(servizioService.save(servizioDto));
    }

    /**
     * @param id Identificativo del servizio da eliminare.
     */
    @Operation(summary = "delete", description = "Elimina un servizio")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Optional.ofNullable(servizioService.findById(id)).orElseThrow(() -> {
            return new ResourceNotFoundException();
        });
        servizioService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @param servizioDto Dto contenente i dati del servizio da aggiornare.
     * @param id             Identificativo del servizio da aggiornare.
     * @return servizio aggiornato.
     */
    @Operation(summary = "update", description = "Aggiorna un servizio")
    @PutMapping("/{id}")
    public ResponseEntity<Servizio> update(@RequestBody ServizioDto servizioDto, @PathVariable("id") Long id) {
        return ResponseEntity.ok(servizioService.update(servizioDto, id));
    }

    /**
     * @param probe         Dto contenente i dati del servizio da filtrare.
     * @param page          Pagina dei risultati.
     * @param size          Numero di risultati per pagina.
     * @param sortField     Campo per ordinare i risultati.
     * @param sortDirection Direzione di ordinamento.
     * @return Lista di servizi filtrati.
     */
    @Operation(summary = "filter", description = "Filtra gli abbonamenti")
    @PostMapping("/filter")
    ResponseEntity<Page<Servizio>> filter(
            @RequestBody(required = false) Servizio probe,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        return servizioService.filter(probe, page, size, sortField, sortDirection);
    }
}