package org.pm.aerologixbackend.security;

import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Add CORS
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // SHIPMENTS
                        .requestMatchers(HttpMethod.GET, "/api/shipments").hasAnyRole("ADMIN", "DRIVER")
                        .requestMatchers(HttpMethod.GET, "/api/shipments/*").hasAnyRole("ADMIN", "DRIVER", "CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/shipments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/shipments/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/shipments/*").hasRole("ADMIN")
                        .requestMatchers("/api/shipments/*/assign").hasRole("ADMIN")
                        .requestMatchers("/api/shipments/*/unassign").hasRole("ADMIN")
                        .requestMatchers("/api/shipments/*/deliver").hasAnyRole("ADMIN", "DRIVER")

                        // TRUCKS
                        .requestMatchers(HttpMethod.GET, "/api/trucks/**").hasAnyRole("ADMIN", "DRIVER")
                        .requestMatchers(HttpMethod.POST, "/api/trucks").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/trucks/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/trucks/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/trucks/*/location").hasAnyRole("ADMIN", "DRIVER")
                        .requestMatchers(HttpMethod.DELETE, "/api/trucks/*").hasRole("ADMIN")

                        // DRIVERS
                        .requestMatchers("/api/drivers/**").hasRole("ADMIN")

                        // WAREHOUSES
                        .requestMatchers("/api/warehouses/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}