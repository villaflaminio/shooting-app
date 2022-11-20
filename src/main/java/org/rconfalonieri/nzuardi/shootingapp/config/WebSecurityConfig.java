package org.rconfalonieri.nzuardi.shootingapp.config;


import org.rconfalonieri.nzuardi.shootingapp.exception.JwtAccessDeniedHandler;
import org.rconfalonieri.nzuardi.shootingapp.exception.JwtAuthenticationEntryPoint;
//import org.rconfalonieri.nzuardi.shootingapp.security.CustomAuthenticationProvider;
import org.rconfalonieri.nzuardi.shootingapp.security.jwt.JWTConfigurer;
import org.rconfalonieri.nzuardi.shootingapp.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
//    @Autowired
//    private CustomAuthenticationProvider authProvider;

    public WebSecurityConfig(
            TokenProvider tokenProvider,
            CorsFilter corsFilter,
            JwtAuthenticationEntryPoint authenticationErrorHandler,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.authenticationErrorHandler = authenticationErrorHandler;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    // Configure BCrypt password encoder =====================================================================


    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure paths and requests that should be ignored by Spring Security ================================



//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authProvider);
//    }
    @Override
    public void configure(WebSecurity web) {

        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/swagger-ui/**", "/bus/v3/api-docs/**")
                // allow anonymous resource requests
                .antMatchers(
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/h2-console/**"
                );
    }


    // Configure security settings ===========================================================================

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and()
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()

                //  .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(authenticationErrorHandler)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // enable h2-console
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // create no session
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                //todo aggiungere tutti i permessi per le varie rotte

                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/api/user/**").hasAuthority("ROLE_USER")
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .apply(securityConfigurerAdapter());
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
}
