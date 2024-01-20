package net.sparkminds.librarymanagement.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                        .requestMatchers("api/v1/**").permitAll()        // Allow unauthenticated access to GET requests under "api/v1/"
                        .requestMatchers("/api/v1/auth/**").permitAll()        // Allow unauthenticated access to requests under "/api/v1/auth/"
                        .anyRequest().authenticated()        // Require authentication for any other request
                );

        return http.getOrBuild();        // Build and return the SecurityFilterChain
    }
}
