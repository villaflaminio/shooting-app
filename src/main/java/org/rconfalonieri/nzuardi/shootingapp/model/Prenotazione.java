package org.rconfalonieri.nzuardi.shootingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "prenotazione")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Prenotazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "data_inizio", nullable = false)
    @NotNull
    private LocalDate dataInizio;

    @Column(name = "data_fine", nullable = false)
    @NotNull
    private LocalDate dataFine;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "utente_id", referencedColumnName = "id")
    private Utente utente;
}