package org.rconfalonieri.nzuardi.shootingapp.security.jwt;

import com.google.gson.Gson;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.rconfalonieri.nzuardi.shootingapp.model.dto.AuthResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

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
    public String createToken(Authentication authentication, boolean rememberMe) {
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
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date())
                .setHeaderParam("typ", "JWT")
                .setAudience("secure-app")

                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
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

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
//    /**
//     * Create a JWT token.
//     * @param authentication The authentication object.
//     * @return The JWT token.
//     */
//    public String createToken(Authentication authentication) {
//        // Get the user principal
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//
//        // Get the user roles
//        List<String> roles = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
//
//        // Get today.
//        Date now = new Date();
//
//        // Get the expiration date.
//        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());
//        String signingKey = appProperties.getAuth().getTokenSecret();
//
//        // Create the token.
//        return Jwts.builder()
//                .signWith(key, SignatureAlgorithm.HS512)
//                .setHeaderParam("typ", "JWT")
//                .setIssuedAt(new Date())
//                .setAudience("secure-app")
//                .setSubject(Long.toString(userPrincipal.getId()))
//                .setExpiration(expiryDate)
//                .claim("rol", roles)
//                .compact();
//
//    }
//
//    /**
//     * Create authentication response.
//     * @param authentication The authentication object.
//     * @param response The HTTP response.
//     * @return The authentication response.
//     */
//    public HttpServletResponse createAuthResponse(Authentication authentication, HttpServletResponse response) {
//        // Get the user principal
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Create the token and refresh token.
//        String token = createToken(authentication);
//        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
//        response.addHeader("Authorization", "Bearer " + token);
//
//        try{
//            // Prepare repsonse to send to FE with username, authorities and duration of the token.
//            response.setHeader("Access-Control-Expose-Headers", "*");
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//
//            // Create the response object.
//            AuthResponseDto authResponseDto =  AuthResponseDto.builder()
//                    .id(user.getId())
//                    .email(user.getEmail())
//                    .name(user.getName())
//                    .role(user.getAuthorities())
//                    .token(token)
//                    .refreshToken(refreshToken.getToken())
//                    .duration(Long.toString(appProperties.getAuth().getTokenExpirationMsec()))
//                    .build();
//            Gson gson = new Gson();
//            response.getWriter().write(gson.toJson(authResponseDto));
//
//        }catch ( Exception e){
//            e.printStackTrace();
//        }
//        return response;
//    }
//
//    /**
//     * Generate token from the given user.
//     * @param user The user.
//     * @return the token.
//     */
//    public String generateTokenFromUser(User user) {
//        // Get the user roles
//        List<String> roles = new ArrayList<>();
//        for(Role r : user.getRoles()){
//            roles.add(r.getName());
//        }
//        Date now = new Date();
//
//        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());
//        String signingKey = appProperties.getAuth().getTokenSecret();
//
//        // Create the token.
//        return Jwts.builder()
//                .signWith(key, SignatureAlgorithm.HS512)
//                .setHeaderParam("typ", "JWT")
//                .setIssuedAt(new Date())
//                .setAudience("secure-app")
//                .setSubject(Long.toString(user.getId()))
//                .setExpiration(expiryDate)
//                .claim("rol", roles)
//                .compact();
//    }
//
//    /**
//     * Generate authentication from the user.
//     * @param user The user.
//     * @return The authentication.
//     */
//    public AuthResponseDto generateAuthFromUser(User user) {
//        // Get token and refresh token.
//        String token = generateTokenFromUser(user);
//        try{
//            // Prepare repsonse to send to FE with username, authorities and duration of the token.
//            return AuthResponseDto.builder()
//                    .id(user.getId())
//                    .email(user.getEmail())
//                    .name(user.getName())
//                    .role(user.getAuthorities())
//                    .token(token)
//                    .duration(Long.toString(appProperties.getAuth().getTokenExpirationMsec()))
//                    .build();
//
//        }catch ( Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * Get the user id from the token.
//     * @param token The token.
//     * @return The user id.
//     */
//    public Long getUserIdFromToken(String token) {
//        Claims claims = Jwts.parser()
//                .signWith(key, SignatureAlgorithm.HS512)
//                .parseClaimsJws(token)
//                .getBody();
//
//        return Long.parseLong(claims.getSubject());
//    }
//
//    /**
//     * Validate the token.
//     * @param authToken The token to be validated.
//     * @return true if the token is valid, false otherwise.
//     */
//

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
}
