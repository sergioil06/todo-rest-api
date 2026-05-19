package com.openwebinars.todo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openwebinars.todo.users.User;
import com.openwebinars.todo.users.UserResponseDto;
import com.openwebinars.todo.users.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Admin", description = "Endpoints exclusivos para el Administrador")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @Operation(summary = "Listar todos los usuarios del sistema")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers()
                .stream()
                .map(u -> UserResponseDto.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .role(u.getRole().name())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Promocionar un usuario estándar a rol GESTOR")
    @PutMapping("/promote/{userId}")
    public ResponseEntity<UserResponseDto> promoteToGestor(@PathVariable Long userId) {
        User updatedUser = userService.cambiarRolAGestor(userId);
        UserResponseDto dto = UserResponseDto.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .role(updatedUser.getRole().name())
                .build();
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Degradar un usuario gestor a rol USER estándar")
    @PutMapping("/demote/{userId}")
    public ResponseEntity<UserResponseDto> demoteToUser(@PathVariable Long userId) {
        User updatedUser = userService.cambiarRolAUsuario(userId);
        UserResponseDto dto = UserResponseDto.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .role(updatedUser.getRole().name())
                .build();
        return ResponseEntity.ok(dto);
    }
}
