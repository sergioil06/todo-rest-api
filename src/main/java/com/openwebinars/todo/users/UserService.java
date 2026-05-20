package com.openwebinars.todo.users;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openwebinars.todo.dto.EditProfileDto;
import com.openwebinars.todo.error.UserNotFoundException;
import com.openwebinars.todo.model.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public User save(NewUserRequestDto newUser) {
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setFullname(newUser.getFullname());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    public UserResponseDto register(NewUserRequestDto req) {
        User u = User.builder()
            .username(req.getUsername())
            .password(passwordEncoder.encode(req.getPassword()))
            .email(req.getEmail())
            .fullname(req.getFullname())
            .role(UserRole.USER)
            .build();
        User saved = userRepository.save(u);
        return modelMapper.map(saved, UserResponseDto.class);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User buscarPorUsername(String username) {
        return userRepository.findFirstByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
    }

    public User promoverUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));
        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.GESTOR);
        } else if (user.getRole() == UserRole.GESTOR) {
            user.setRole(UserRole.ADMIN);
        }
        return userRepository.save(user);
    }

    public User degradarUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));
        if (user.getRole() == UserRole.ADMIN) {
            user.setRole(UserRole.GESTOR);
        } else if (user.getRole() == UserRole.GESTOR) {
            user.setRole(UserRole.USER);
        }
        return userRepository.save(user);
    }

    public User cambiarRolAGestor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));
        user.setRole(UserRole.GESTOR);
        return userRepository.save(user);
    }

    public User cambiarRolAUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    public User actualizarPerfil(EditProfileDto cmd, User userLogueado) {
        User user = userRepository.findById(userLogueado.getId())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userLogueado.getId()));
        if (cmd.getEmail() != null && !cmd.getEmail().isBlank()) {
            user.setEmail(cmd.getEmail());
        }
        if (cmd.getFullname() != null && !cmd.getFullname().isBlank()) {
            user.setFullname(cmd.getFullname());
        }
        return userRepository.save(user);
    }

    public UserResponseDto login(User userLogueado) {
        return modelMapper.map(userLogueado, UserResponseDto.class);
    }
}