package org.rconfalonieri.nzuardi.shootingapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "valutazione")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Valutazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(mappedBy = "valutazione")
    private User utenteValutato;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "istruttore_id", referencedColumnName = "id")
    private Istruttore utenteValutante;

    @Column(name="voto")
    @NotNull
    @Min(1)
    @Max(5)
    private Integer voto;


}