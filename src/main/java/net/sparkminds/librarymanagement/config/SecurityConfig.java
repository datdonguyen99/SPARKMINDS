package net.sparkminds.librarymanagement.config;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.security.JwtAuthenticationEntryPoint;
import net.sparkminds.librarymanagement.security.JwtAuthenticationFilter;
import net.sparkminds.librarymanagement.service.impl.AccountServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AccountServiceImpl accountService;

    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter authenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(accountService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * authentication manager bean
     *
     * @param authenticationConfiguration authenticationConfiguration
     * @return An instance of {@link AuthenticationManager}
     * @throws Exception java.lang.exception
     * @see AuthenticationManager
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * security filter chain
     *
     * @param http http
     * @return An instance of {@link SecurityFilterChain}
     * @throws Exception java.lang.exception
     * @see SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)        // Disable CSRF protection
                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("api/v1/**").permitAll()        // Allow unauthenticated access to GET requests under "api/v1/"
                        .requestMatchers("/api/v1/common/**").permitAll()        // Allow unauthenticated access to requests under "/api/v1/common/"
                        .requestMatchers("/api/v1/user/**").hasRole("USER")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())        // Require authentication for any other request
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.getOrBuild();        // Build and return the SecurityFilterChain
    }
}
