package org.rconfalonieri.nzuardi.shootingapp.security.jwt;

import org.rconfalonieri.nzuardi.shootingapp.security.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtra le richieste in arrivo ed aggiunge Spring Security
 * per gli header che corrispondono ad un utente valido
 */
public class JWTFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Logger LOG = LoggerFactory.getLogger(JWTFilter.class);
    private final TokenProvider tokenProvider;


    public JWTFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * il token viene validato da tokenProvideUSERr
     * Authentication viene inserita nel SecurityContextHolder
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // Get the JWT from the request.
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Long userId = tokenProvider.getUserIdFromToken(jwt);

                UserDetails userDetails = tokenProvider.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOG.debug("set Authentication to security context for '{}'", authentication.getName());
            }

        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // Continue down the chain.
        filterChain.doFilter(request, response);
    }

    /**
     * Method to get the JWT from the request.
     *
     * @param request The request received.
     * @return The JWT.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
