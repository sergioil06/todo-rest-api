package com.openwebinars.todo;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.openwebinars.todo.dto.TagRequestDto;
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.model.TaskPriority;
import com.openwebinars.todo.model.UserRole;
import com.openwebinars.todo.repos.CategoryRepository;
import com.openwebinars.todo.repos.TaskRepository;
import com.openwebinars.todo.service.TagService;
import com.openwebinars.todo.users.User;
import com.openwebinars.todo.users.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private TagService tagService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        if (userRepository.count() == 0) {
        	User admin = User.builder()
        	        .username("admin2")
        	        .password(passwordEncoder.encode("1234"))
        	        .email("admin@todo.com")
        	        .role(UserRole.ADMIN)
        	        .build();

        	User user = User.builder()
        	        .username("sergio2")
        	        .password(passwordEncoder.encode("1234"))
        	        .email("sergio@todo.com")
        	        .role(UserRole.USER)
        	        .build();

            userRepository.save(admin);
            userRepository.save(user);

            Category cat1 = new Category();
            cat1.setTitle("Trabajo");
            cat1.setTasks(new ArrayList<>());
            categoryRepository.save(cat1);

            Tag tagUrgente = tagService.save(new TagRequestDto("Urgente"));
            Tag tagTrabajo = tagService.save(new TagRequestDto("Personal"));

            Task t1 = Task.builder()
                    .title("Proyecto Final")
                    .description("Revisar CRUD de tareas")
                    .priority(TaskPriority.ALTA)
                    .completed(false)
                    .createdAt(LocalDateTime.now())
                    .author(user)
                    .category(cat1)
                    .build();

            t1.getTags().add(tagUrgente);

            taskRepository.save(t1);

            System.out.println("--- Datos de prueba cargados correctamente ---");
        }
    }
}