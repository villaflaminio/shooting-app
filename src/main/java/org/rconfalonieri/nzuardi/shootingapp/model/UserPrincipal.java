package org.rconfalonieri.nzuardi.shootingapp.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the user principal.
 */
public class UserPrincipal implements  UserDetails {
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    /**
     * Instantiates a new User principal.
     * @param id the id to set
     * @param email the email to set
     * @param password the password to set
     * @param authorities the authorities to set
     */
    public UserPrincipal(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Instantiates a new User principal.
     * @param user the user to create
     */
    public static UserPrincipal create(User user) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Authority authority : user.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(authority.getName()));
        }

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }


    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
