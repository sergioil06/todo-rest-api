package com.openwebinars.todo.security;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.openwebinars.todo.error.CustomAccessDeniedHandler;
import com.openwebinars.todo.error.CustomAuthenticationEntryPoint;
import org.springframework.http.HttpMethod;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/auth/register",
                    "/auth/login"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/category/**", "/category").authenticated()
                .requestMatchers(HttpMethod.POST, "/category/**", "/category").hasAnyRole("ADMIN", "GESTOR")
                .requestMatchers(HttpMethod.PUT, "/category/**", "/category").hasAnyRole("ADMIN", "GESTOR")
                .requestMatchers(HttpMethod.DELETE, "/category/**", "/category").hasAnyRole("ADMIN", "GESTOR")
                .anyRequest().authenticated()
            );

        return http.build();
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
    
   