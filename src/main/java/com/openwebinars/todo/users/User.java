package com.openwebinars.todo.users;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.model.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_entity")
//... anotaciones existentes ...
public class User implements UserDetails {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(unique = true, nullable = false)
 private String username;
 
 private String password; // Esta se guardará encriptada con BCrypt
 
 private String email;
 private String fullname; // [cite: 21]

 @Enumerated(EnumType.STRING)
 private UserRole role; // Los valores: ADMIN, GESTOR, USER [cite: 105]

 @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
 private List<Task> tasks;

 // --- SEGURIDAD ---

 @Override
 public Collection<? extends GrantedAuthority> getAuthorities() {
     // Solo una autoridad basada en el Rol del proyecto [cite: 105]
     return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
 }

 // El resto de métodos de UserDetails (isAccountNonExpired, etc.) 
 // están perfectos devolviendo true como los tienes.
}
