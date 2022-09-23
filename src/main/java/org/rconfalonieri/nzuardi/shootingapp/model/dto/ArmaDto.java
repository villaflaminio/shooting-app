package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import lombok.*;
import org.rconfalonieri.nzuardi.shootingapp.model.Arma;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link Arma} entity
 */
@Data
@Getter
@Setter
public class ArmaDto implements Serializable {
    private Long id;
    @NotNull
    @Size(min = 1, max = 50)
    private String nome;
    private String foto;
    @NotNull
    private boolean disponibile;
    @NotNull
    @Size(min = 1, max = 50)
    private String seriale;
}