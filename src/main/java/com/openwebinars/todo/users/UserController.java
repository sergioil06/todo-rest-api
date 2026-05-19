package com.openwebinars.todo.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.openwebinars.todo.dto.EditProfileCommand;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody NewUserRequestDto newUser) {
        UserResponseDto saved = userService.register(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Modificar los datos de mi perfil (Email o Nombre completo)")
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'GESTOR')")
    public ResponseEntity<UserResponseDto> updateProfile(
            @RequestBody EditProfileCommand cmd,
            @AuthenticationPrincipal User userLogueado
    ) {
        User updatedUser = userService.actualizarPerfil(cmd, userLogueado);
        UserResponseDto dto = UserResponseDto.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .role(updatedUser.getRole().name())
                .build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserResponseDto> login(@AuthenticationPrincipal User userLogueado) {
        return ResponseEntity.ok(userService.login(userLogueado));
    }
}
