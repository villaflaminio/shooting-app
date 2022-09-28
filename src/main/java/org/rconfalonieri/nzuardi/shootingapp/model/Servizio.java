package org.rconfalonieri.nzuardi.shootingapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Blob;
import java.util.List;

@Entity
@Table(name = "servizio")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Servizio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="nome")
    @NotNull
    @Size(min = 1, max = 50)
    private String nome;

    @Column(name="prezzo")
    @NotNull
    private Float prezzo;

    @Lob
    private String foto;

    @Column(name="quantita")
    @NotNull
    private Integer quantita;

    @ManyToOne
    @JoinColumn(name = "prenotazione_id")
    private Prenotazione prenotazione;

    @ManyToMany
    @JoinTable(
            name = "servizi_prenotazione",
            joinColumns = @JoinColumn(name = "servizio_id"),
            inverseJoinColumns = @JoinColumn(name = "prenotazione_id"))
    List<Prenotazione> prenotazioni;

}