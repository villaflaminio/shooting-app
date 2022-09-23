package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.rconfalonieri.nzuardi.shootingapp.model.Istruttore;

import java.io.Serializable;

/**
 * A DTO for the {@link Istruttore} entity
 */
@Data
@Setter
@Getter
public class IstruttoreDto implements Serializable {
    private Long id;
}