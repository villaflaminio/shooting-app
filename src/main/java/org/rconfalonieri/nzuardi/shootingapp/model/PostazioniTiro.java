package org.rconfalonieri.nzuardi.shootingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "postazione_tiro")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE postazione_tiro SET deleted = true WHERE id=?")
public class PostazioniTiro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "sagoma")
    @NotNull
    private boolean sagoma;

    @ManyToOne()
    @JoinColumn(name = "banchina_id")
    private Banchina banchina;

    @Column(name = "distanza", nullable = false)
    @NotNull
    private Integer distanza;

    private boolean attiva;

    @OneToMany(mappedBy="postazioniTiro")
    private List<Prenotazione> prenotazioni;

    private boolean deleted = Boolean.FALSE;

}