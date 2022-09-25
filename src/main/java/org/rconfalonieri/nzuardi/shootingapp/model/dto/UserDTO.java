package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rconfalonieri.nzuardi.shootingapp.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    public Long id;
    public String password;
    public String email;
    public String nome;
    public String cognome;

    public UserDTO(User user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.cognome = user.getCognome();
    }
    public UserDTO( String password, String role, String nome, String cognome) {
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
    }

}