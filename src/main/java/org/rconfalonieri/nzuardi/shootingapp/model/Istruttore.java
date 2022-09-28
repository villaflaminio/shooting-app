package org.rconfalonieri.nzuardi.shootingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "istruttore")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE istruttore SET deleted = true WHERE id=?")
public class Istruttore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nome", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String nome;

    @Column(name = "cognome", length = 50)
    @NotNull
    @Size(min = 1, max = 50)
    private String cognome;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prenotazione_id", referencedColumnName = "id")
    private Prenotazione prenotazione;

    @OneToOne(mappedBy = "utenteValutato")
    private Valutazione valutazione;


    private boolean deleted = Boolean.FALSE;

}