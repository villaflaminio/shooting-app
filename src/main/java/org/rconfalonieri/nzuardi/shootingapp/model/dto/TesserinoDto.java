package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * A DTO for the {@link org.rconfalonieri.nzuardi.shootingapp.model.Tesserino} entity
 */
@Data
@Getter
@Setter
public class TesserinoDto implements Serializable {
    private Long id;
    @NotNull
    private Date dataScadenza;
    @NotNull
    private Date dataRilascio;
    @NotNull
    private String qrCode;
}