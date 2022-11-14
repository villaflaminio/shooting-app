package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * A DTO for the {@link org.rconfalonieri.nzuardi.shootingapp.model.Prenotazione} entity
 */
@Data
@Setter
@Getter
public class PrenotazioneDto implements Serializable {
    private Long id;
    @NotNull
    private Date dataInizio;
    @NotNull
    private Date dataFine;

    private long idUtente;
    private long idPostazioneTiro;
    private List<Long> idServiziExtra;
    private List<Long> idArmi;
}