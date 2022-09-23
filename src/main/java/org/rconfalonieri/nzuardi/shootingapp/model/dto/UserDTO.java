package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.rconfalonieri.nzuardi.shootingapp.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserDTO {

    public Long id;
    public String password;
    public String role;
    public String nome;
    public String cognome;
    public User callUser;

    public UserDTO(User user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.cognome = user.getCognome();
    }

    public UserDTO( String password, String role, String nome, String cognome) {
        this.password = password;
        this.role = role;
        this.nome = nome;
        this.cognome = cognome;
    }


}