package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for the {@link org.rconfalonieri.nzuardi.shootingapp.model.PostazioniTiro} entity
 */
@Data
@Setter
@Getter
public class PostazioniTiroDto implements Serializable {
    private Long id;
    @NotNull
    private Boolean sagoma;
    @NotNull
    private Integer distanza;
}