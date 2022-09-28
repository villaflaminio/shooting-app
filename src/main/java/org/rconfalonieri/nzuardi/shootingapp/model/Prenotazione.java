package org.rconfalonieri.nzuardi.shootingapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JoinColumn(name = "utente_id", nullable = false)
    @JsonBackReference
    private User utentePren;

    @ManyToMany(mappedBy = "prenotazioni")
    private List<Servizio> serviziExtra;

    @OneToOne(mappedBy = "prenotazione")
    private Istruttore istruttore;

    @OneToMany(mappedBy = "prenotazione")
    private List<Arma> armi;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "valutazione_id", referencedColumnName = "id")
    private Valutazione valutazione;

    @ManyToOne
    @JoinColumn(name="postazione_tiro_id", nullable=false)
    private PostazioniTiro postazioniTiro;

}