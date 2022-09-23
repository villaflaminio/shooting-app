package org.rconfalonieri.nzuardi.shootingapp.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "utente")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

    @JsonIgnore
    @Column(name = "password", length = 100)
    @NotNull
    @Size(min = 4, max = 100)
    private String password;

    @JsonIgnore
    @Column(name = "sospeso")
    @NotNull
    private boolean sospeso;

    @OneToMany(mappedBy = "utente")
    private List<Tesserino> tesserini;

    @OneToMany(mappedBy = "utentePren")
    private List<Prenotazione> prenotazioni;

    @OneToOne(mappedBy = "utenteValutato")
    private Valutazione valutazione;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "nome_authority", referencedColumnName = "nome")})
    @BatchSize(size = 20)
    private Set<Authority> authorities = new HashSet<>();
}