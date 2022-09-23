package org.rconfalonieri.nzuardi.shootingapp.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

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
    public static UserPrincipal create(User user, String username) {
        List<GrantedAuthority> authorities = getAuthorities(user);
        return new UserPrincipal(
                user.getId(),
                username,
                user.getPassword(),
                authorities
        );
    }
    public static List<GrantedAuthority> getAuthorities(User authority) {

        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        for (Authority singleAuthority: authority.getAuthorities()) {
            list.add(new SimpleGrantedAuthority(singleAuthority.getName()));
        }
        return list;
    }
    /**
     * Instantiates a new User principal.
     * @param user the user to create
     * @param attributes the attributes to set
     * @return the user principal
     */
    public static UserPrincipal create(User user, String username, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user, username);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    public Long getId() {
        return id;
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
