package org.rconfalonieri.nzuardi.shootingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "istruttore")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Istruttore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prenotazione_id", referencedColumnName = "id")
    private Prenotazione prenotazione;

    @OneToOne(mappedBy = "utenteValutante")
    private Valutazione valutazione;

}