package org.rconfalonieri.nzuardi.shootingapp.security;


import lombok.AllArgsConstructor;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
import org.rconfalonieri.nzuardi.shootingapp.model.UserPrincipal;
import org.rconfalonieri.nzuardi.shootingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;


/**
 * Service to handle user details.
 */
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    private Environment env;


    /**
     * Load user by email.
     * @param username The username.
     * @return The user details.
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findActiveTesserinoid(username).orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email : " + username)
        );

        return UserPrincipal.create(user,username);
    }


}