package org.rconfalonieri.nzuardi.shootingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.model.Tesserino;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.TesserinoDto;
import org.rconfalonieri.nzuardi.shootingapp.service.TesserinoService;
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
@RequestMapping("/api/tesserino")
@RestController
@Tag(name = "Tesserino")
public class TesserinoController {

    @Autowired
    TesserinoService tesserinoService;

    /**
     * @return Tutti gli abbonamenti presenti nel database.
     */
    @Operation(summary = "findAll", description = "Ottieni tutti gli abbonamenti")
    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(tesserinoService.findAll());
    }

    /**
     * @param id Identificativo del tesserino da ottenere.
     * @return tesserino con  identificativo specificato.
     */
    @Operation(summary = "findById", description = "Ritorna tesserino con identificativo passato")
    @GetMapping("/{id}")
    public ResponseEntity<Tesserino> findById(@PathVariable("id") Long id) {
        Tesserino tesserino = tesserinoService.findById(id);
        return ResponseEntity.ok(tesserino);
    }

    /**
     * @param tesserinoDto Dto contenente i dati del tesserino da inserire.
     * @return tesserino inserito.
     */
    @Operation(summary = "save", description = "Crea un nuovo tesserino")
    @PostMapping
    public ResponseEntity<Tesserino> save(@RequestBody @Validated TesserinoDto tesserinoDto) {
        return ResponseEntity.ok(tesserinoService.save(tesserinoDto));
    }

    /**
     * @param id Identificativo del tesserino da eliminare.
     */
    @Operation(summary = "delete", description = "Elimina un tesserino")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Optional.ofNullable(tesserinoService.findById(id)).orElseThrow(() -> {
            return new ResourceNotFoundException();
        });
        tesserinoService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @param tesserinoDto Dto contenente i dati del tesserino da aggiornare.
     * @param id             Identificativo del tesserino da aggiornare.
     * @return tesserino aggiornato.
     */
    @Operation(summary = "update", description = "Aggiorna un tesserino")
    @PutMapping("/{id}")
    public ResponseEntity<Tesserino> update(@RequestBody TesserinoDto tesserinoDto, @PathVariable("id") Long id) {
        return ResponseEntity.ok(tesserinoService.update(tesserinoDto, id));
    }

    /**
     * @param probe         Dto contenente i dati del tesserino da filtrare.
     * @param page          Pagina dei risultati.
     * @param size          Numero di risultati per pagina.
     * @param sortField     Campo per ordinare i risultati.
     * @param sortDirection Direzione di ordinamento.
     * @return Lista di tesserini filtrati.
     */
    @Operation(summary = "filter", description = "Filtra i tesserini")
    @PostMapping("/filter")
    ResponseEntity<Page<Tesserino>> filter(
            @RequestBody(required = false) Tesserino probe,
            @RequestParam(required = false, defaultValue = "0", name = "page") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(required = false, name = "sortDirection") String sortDirection) {
        return tesserinoService.filter(probe, page, size, sortField, sortDirection);
    }
}