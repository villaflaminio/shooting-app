package org.rconfalonieri.nzuardi.shootingapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "arma")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Arma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="nome")
    @NotNull
    @Size(min = 1, max = 50)
    private String nome;

    @Lob
    private String foto;

    @Column(name = "disponibile")
    @NotNull
    private boolean disponibile;

    @Column(name="seriale", unique = true)
    @NotNull
    @Size(min = 1, max = 50)
    private String seriale;

    @ManyToOne
    @JoinColumn(name = "prenotazione_id")
    private Prenotazione prenotazione;
}