package org.rconfalonieri.nzuardi.shootingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "tesserino")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tesserino {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "data_scadenza")
    @NotNull
    private Date dataScadenza;

    @Column(name = "data_rilascio")
    @NotNull
    private Date dataRilascio;

    @Column(name = "qr_code")
    @NotNull
    private String qrCode;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private User utente;

}