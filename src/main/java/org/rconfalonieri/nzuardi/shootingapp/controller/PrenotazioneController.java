package org.rconfalonieri.nzuardi.shootingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.model.Prenotazione;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.PrenotazioneDto;
import org.rconfalonieri.nzuardi.shootingapp.service.PrenotazioneService;
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
@RequestMapping("/api/prenotazione")
@RestController
@Tag(name = "Prenotazione")
public class PrenotazioneController {

    @Autowired
    PrenotazioneService prenotazioneService;

    /**
     * @return Tutti gli abbonamenti presenti nel database.
     */
    @Operation(summary = "findAll", description = "Ottieni tutti gli abbonamenti")
    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(prenotazioneService.findAll());
    }

    /**
     * @param id Identificativo del prenotazione da ottenere.
     * @return prenotazione con  identificativo specificato.
     */
    @Operation(summary = "findById", description = "Ritorna prenotazione con identificativo passato")
    @GetMapping("/{id}")
    public ResponseEntity<Prenotazione> findById(@PathVariable("id") Long id) {
        Prenotazione prenotazione = prenotazioneService.findById(id);
        return ResponseEntity.ok(prenotazione);
    }

    /**
     * @param prenotazioneDto Dto contenente i dati del prenotazione da inserire.
     * @return prenotazione inserito.
     */
    @Operation(summary = "save", description = "Crea un nuovo prenotazione")
    @PostMapping
    public ResponseEntity<Prenotazione> save(@RequestBody @Validated PrenotazioneDto prenotazioneDto) {
        return ResponseEntity.ok(prenotazioneService.save(prenotazioneDto));
    }

    /**
     * @param id Identificativo del prenotazione da eliminare.
     */
    @Operation(summary = "delete", description = "Elimina un prenotazione")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Optional.ofNullable(prenotazioneService.findById(id)).orElseThrow(() -> {
            return new ResourceNotFoundException();
        });
        prenotazioneService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @param prenotazioneDto Dto contenente i dati del prenotazione da aggiornare.
     * @param id             Identificativo del prenotazione da aggiornare.
     * @return prenotazione aggiornato.
     */
    @Operation(summary = "update", description = "Aggiorna un prenotazione")
    @PutMapping("/{id}")
    public ResponseEntity<Prenotazione> update(@RequestBody PrenotazioneDto prenotazioneDto, @PathVariable("id") Long id) {
        return ResponseEntity.ok(prenotazioneService.update(prenotazioneDto, id));
    }

    /**
     * @param probe         Dto contenente i dati del prenotazione da filtrare.
     * @param page          Pagina dei risultati.
     * @param size          Numero di risultati per pagina.
     * @param sortField     Campo per ordinare i risultati.
     * @param sortDirection Direzione di ordinamento.
     * @return Lista di servizi filtrati.
     */
    @Operation(summary = "filter", description = "Filtra gli abbonamenti")
    @PostMapping("/filter")
    ResponseEntity<Page<Prenotazione>> filter(
            @RequestBody(required = false) Prenotazione probe,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        return prenotazioneService.filter(probe, page, size, sortField, sortDirection);
    }

}