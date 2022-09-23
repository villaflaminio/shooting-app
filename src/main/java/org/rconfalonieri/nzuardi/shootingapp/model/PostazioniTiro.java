package org.rconfalonieri.nzuardi.shootingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "postazione_tiro")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostazioniTiro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "sagomaq")
    @NotNull
    private boolean sagoma;

    @ManyToOne
    @JoinColumn(name = "banchina_id")
    private Banchina banchina;

    @Column(name = "distanza", nullable = false)
    @NotNull
    private Integer distanza;

}