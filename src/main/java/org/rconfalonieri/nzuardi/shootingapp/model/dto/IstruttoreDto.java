package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.rconfalonieri.nzuardi.shootingapp.model.Istruttore;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link Istruttore} entity
 */
@Data
@Getter
@Setter
public class IstruttoreDto implements Serializable {
    private Long id;
    @NotNull
    @Size(min = 1, max = 50)
    private String nome;
    @NotNull
    @Size(min = 1, max = 50)
    private String cognome;
}