package org.rconfalonieri.nzuardi.shootingapp.model.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MailResponse {
    private String message;
    private Boolean status;
}
