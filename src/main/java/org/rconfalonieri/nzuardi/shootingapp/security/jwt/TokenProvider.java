package org.rconfalonieri.nzuardi.shootingapp.security.jwt;

import com.google.gson.Gson;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.rconfalonieri.nzuardi.shootingapp.model.User;
//import org.rconfalonieri.nzuardi.shootingapp.model.UserPrincipal;
import org.rconfalonieri.nzuardi.shootingapp.model.UserPrincipal;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.AuthResponseDto;
import org.rconfalonieri.nzuardi.shootingapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    /**
     * Logger serve per scrivere i log durante l'esecuzione del programma
     **/
    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);
    private final String base64Secret;
    private final long tokenValidityInMilliseconds;
    private final long tokenValidityInMillisecondsForRememberMe;

    private Key key;
    @Autowired
    private UserRepository userRepository;

    /**
     * Da application.yml prendo i valori che indicano la durata dei token
     **/
    public TokenProvider(
            @Value("${jwt.base64-secret}") String base64Secret, // chiave per criptare
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
            @Value("${jwt.token-validity-in-seconds-for-remember-me}") long tokenValidityInSecondsForRememberMe) {
        this.base64Secret = base64Secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.tokenValidityInMillisecondsForRememberMe = tokenValidityInSecondsForRememberMe * 1000;
    }

    /**
     * Dopo che e' stato instanziato il tokenProvider setto la chiave base64Secret nella variabile globale key
     **/
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * rememberMe indica che il token dell'utente deve avere durata maggiore
     * <p>
     * UserRestController con api/authenticate
     **/
    public ResponseEntity<?> createToken(Authentication authentication, User user, boolean rememberMe) {
        // Get the user principal
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }


        String token =  Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date())
                .setHeaderParam("typ", "JWT")
                .setAudience("secure-app")
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();

        AuthResponseDto authResponseDto =  AuthResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getNome())
                .authorities(user.getAuthorities())
                .token(token)
                .expiration(validity)
                .build();


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + token);
        httpHeaders.set("Access-Control-Expose-Headers", "*");

        return new ResponseEntity<>(authResponseDto, httpHeaders, HttpStatus.OK);

    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User user = userRepository.findById(Long.parseLong(claims.getSubject())).orElseThrow(() -> new RuntimeException("User not found"));
        return new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }

    /**
     * Load user by id.
     * @param id The id of the user.
     * @return The UserDetails.
     */
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User")
        );

        return UserPrincipal.create(user);
    }
}
