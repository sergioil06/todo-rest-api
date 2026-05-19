package com.openwebinars.todo;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.openwebinars.todo.dto.TagRequest; // IMPORTANTE: Importamos el DTO de Tag
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.model.TaskPriority;
import com.openwebinars.todo.model.UserRole;
import com.openwebinars.todo.repos.CategoryRepository;
import com.openwebinars.todo.repos.TaskRepository;
import com.openwebinars.todo.service.TagService; // IMPORTANTE: Inyectaremos el servicio ahora
import com.openwebinars.todo.users.User;
import com.openwebinars.todo.users.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private TagService tagService; // Cambiado de TagRepository a TagService para usar el DTO
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        if (userRepository.count() == 0) {
            // 1. Usuarios con password cifrada
        	User admin = User.builder()
        	        .username("admin2") // <--- Cámbialo a admin2
        	        .password(passwordEncoder.encode("1234")) 
        	        .email("admin@todo.com")
        	        .role(UserRole.ADMIN)
        	        .build();

        	User user = User.builder()
        	        .username("sergio2") // <--- Cámbialo a sergio2
        	        .password(passwordEncoder.encode("1234"))
        	        .email("sergio@todo.com")
        	        .role(UserRole.USER)
        	        .build();

            userRepository.save(admin);
            userRepository.save(user);

            // 2. Categorías
            Category cat1 = new Category();
            cat1.setTitle("Trabajo");
            cat1.setTasks(new ArrayList<>()); // Evita que empiece en null
            categoryRepository.save(cat1);

            // 3. Tags (Usando el nuevo servicio con TagRequest)
            // Esto garantiza que el flujo pase por el DTO tal y como lo pide el estándar
            Tag tagUrgente = tagService.save(new TagRequest("Urgente"));
            Tag tagTrabajo = tagService.save(new TagRequest("Personal"));

            // 4. Tarea con el Enum TaskPriority
            Task t1 = Task.builder()
                    .title("Proyecto Final")
                    .description("Revisar CRUD de tareas")
                    .priority(TaskPriority.ALTA)
                    .completed(false)
                    .createdAt(LocalDateTime.now())
                    .author(user)
                    .category(cat1)
                    .build();
            
            // Añadimos el tag al set de la tarea (Recuerda tener @Builder.Default en la entidad Task)
            t1.getTags().add(tagUrgente); 

            taskRepository.save(t1);

            System.out.println("--- Datos de prueba cargados correctamente usando DTOs ---");
        }
    }
}