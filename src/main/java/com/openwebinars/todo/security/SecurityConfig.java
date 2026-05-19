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
@EnableMethodSecurity // CRÍTICO: Sin esto, @PreAuthorize en los controladores NO FUNCIONA
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 0. Habilitar CORS enlazando con la configuración global de CorsConfig
            .cors(Customizer.withDefaults()) 
            
            .csrf(csrf -> csrf.disable()) // Deshabilitado porque es una API REST Stateless
            .httpBasic(Customizer.withDefaults()) // Autenticación Básica requerida por el PDF
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Comunicación Stateless
            .authorizeHttpRequests(authz -> authz
                // 1. Endpoints Públicos (Exigido por el PDF)
                .requestMatchers(
                    "/v3/api-docs/**", 
                    "/swagger-ui/**", 
                    "/swagger-ui.html", 
                    "/auth/register", 
                    "/auth/login" // ¡Añadido el login público!
                ).permitAll()
                
                // 2. Gestión de usuarios (Solo ADMIN - Ej: promocionar/degradar)
                .requestMatchers("/admin/**").hasRole("ADMIN") 
                
                // 3. Categorías (Gestión protegida según el PDF)
                // Cualquier usuario autenticado (USER, GESTOR, ADMIN) puede verlas
                .requestMatchers(HttpMethod.GET, "/category/**", "/category").authenticated()
                // Solo ADMIN o GESTOR pueden crear, modificar o borrar categorías
                .requestMatchers(HttpMethod.POST, "/category/**", "/category").hasAnyRole("ADMIN", "GESTOR")
                .requestMatchers(HttpMethod.PUT, "/category/**", "/category").hasAnyRole("ADMIN", "GESTOR")
                .requestMatchers(HttpMethod.DELETE, "/category/**", "/category").hasAnyRole("ADMIN", "GESTOR")
                
                // 4. El resto de endpoints (Tareas, Tags, etc.) requieren autenticación
                .anyRequest().authenticated()
            );
        
        // Manejo de errores de autenticación y autorización para devolver códigos HTTP adecuados
        

        return http.build();
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
    
   