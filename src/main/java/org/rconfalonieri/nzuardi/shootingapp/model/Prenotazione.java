package org.rconfalonieri.nzuardi.shootingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

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
    private Date dataInizio;

    @Column(name = "data_fine", nullable = false)
    @NotNull
    private Date dataFine;

    private boolean abilitata;

    private boolean confermata;
    @ManyToOne
    @JoinColumn(name="utente_id", nullable=false)
    private User utentePren;

    @OneToMany(mappedBy = "prenotazione") //todo prenotazione - servizi many to many
    private List<Servizio> extra;

    @OneToOne(mappedBy = "prenotazione")
    private Istruttore istruttore;

    @OneToMany(mappedBy = "prenotazione")
    private List<Arma> armi;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "valutazione_id", referencedColumnName = "id")
    private Valutazione valutazione;

}