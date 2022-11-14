package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for the {@link org.rconfalonieri.nzuardi.shootingapp.model.Valutazione} entity
 */
@Data
@Getter
@Setter
public class ValutazioneDaUtenteDto implements Serializable {
    private Long id;
    @NotNull
    @Min(1)
    @Max(5)
    private Integer voto;

     private long idIstruttore;
}