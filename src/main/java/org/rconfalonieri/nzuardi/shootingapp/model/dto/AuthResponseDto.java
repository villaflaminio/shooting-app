package org.rconfalonieri.nzuardi.shootingapp.model.dto;

import lombok.Builder;
import lombok.Data;
import org.rconfalonieri.nzuardi.shootingapp.model.Authority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * This class represents the response of the authentication.
 */
@Data
@Builder
public class AuthResponseDto {
    public long id;
    public String email;
    public String name;
    public Set<Authority>  authorities ;

    public String token;
  //  public String refreshToken;
    public Date expiration;

}
