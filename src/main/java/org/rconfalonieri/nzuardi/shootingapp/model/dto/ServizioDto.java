package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link org.rconfalonieri.nzuardi.shootingapp.model.Servizio} entity
 */
@Data
@Getter
@Setter
public class ServizioDto implements Serializable {
    private Long id;
    @NotNull
    @Size(min = 1, max = 50)
    private String nome;
    @NotNull
    private Float prezzo;
    private String foto;
    @NotNull
    private Integer quantita;
}