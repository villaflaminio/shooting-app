package org.rconfalonieri.nzuardi.shootingapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "tesserino")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tesserino {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TESSERINO_SEQ")
    private Long id;

    @Column(name = "data_scadenza")
    @NotNull
    private Date dataScadenza;

    @Column(name = "data_rilascio")
    @NotNull
    private Date dataRilascio;

    @Column(name = "qr_code")
    @NotNull
    @Lob
    private String qrCode;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    @JsonBackReference
    private User utente;

}