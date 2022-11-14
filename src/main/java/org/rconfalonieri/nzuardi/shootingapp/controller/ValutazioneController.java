package org.rconfalonieri.nzuardi.shootingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.model.Valutazione;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ValutazioneDto;
import org.rconfalonieri.nzuardi.shootingapp.service.ValutazioneService;
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
@RequestMapping("/api/valutazione")
@RestController
@Tag(name = "Valutazione")
public class ValutazioneController {


    @Autowired
    ValutazioneService valutazioneService;

    /**
     * @return Tutti gli abbonamenti presenti nel database.
     */
    @Operation(summary = "findAll", description = "Ottieni tutti gli abbonamenti")
    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(valutazioneService.findAll());
    }

    /**
     * @param id Identificativo del valutazione da ottenere.
     * @return valutazione con  identificativo specificato.
     */
    @Operation(summary = "findById", description = "Ritorna valutazione con identificativo passato")
    @GetMapping("/{id}")
    public ResponseEntity<Valutazione> findById(@PathVariable("id") Long id) {
        Valutazione valutazione = valutazioneService.findById(id);
        return ResponseEntity.ok(valutazione);
    }




    /**
     * @param valutazioneDto Dto contenente i dati del valutazione da aggiornare.
     * @param id             Identificativo del valutazione da aggiornare.
     * @return valutazione aggiornato.
     */
    @Operation(summary = "update", description = "Aggiorna un valutazione")
    @PutMapping("/{id}")
    public ResponseEntity<Valutazione> update(@RequestBody ValutazioneDto valutazioneDto, @PathVariable("id") Long id) {
        return ResponseEntity.ok(valutazioneService.update(valutazioneDto, id));
    }

    /**
     * @param probe         Dto contenente i dati del valutazione da filtrare.
     * @param page          Pagina dei risultati.
     * @param size          Numero di risultati per pagina.
     * @param sortField     Campo per ordinare i risultati.
     * @param sortDirection Direzione di ordinamento.
     * @return Lista di valutazioni filtrate.
     */
    @Operation(summary = "filter", description = "Filtra le valutazioni , per filtrare l'istruttore passare l'id nella probe")
    @PostMapping("/filter")
    ResponseEntity<Page<Valutazione>> filter(
            @RequestBody(required = false) Valutazione probe,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        return valutazioneService.filter(probe, page, size, sortField, sortDirection);
    }


}