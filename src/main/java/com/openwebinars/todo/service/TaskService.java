package com.openwebinars.todo.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openwebinars.todo.dto.EditTaskCommand;
import com.openwebinars.todo.dto.TaskDashboardDto;
import com.openwebinars.todo.error.BusinessRuleException;
import com.openwebinars.todo.error.CategoryNotFoundException;
import com.openwebinars.todo.error.TagNotFoundException;
import com.openwebinars.todo.error.TaskNotFoundException;
import com.openwebinars.todo.model.Category;
import com.openwebinars.todo.model.Tag;
import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.repos.CategoryRepository;
import com.openwebinars.todo.repos.TagRepository;
import com.openwebinars.todo.repos.TaskRepository;
import com.openwebinars.todo.users.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Task> findByAuthor(User author) {
        return taskRepository.findByAuthor(author);
    }

    public Task save(EditTaskCommand cmd, User author) {
        if (cmd.deadline() != null && cmd.deadline().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("No puedes crear una tarea con una fecha límite del pasado.");
        }

        Category category = null;
        if (cmd.categoryId() != null) {
            category = categoryRepository.findById(cmd.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(cmd.categoryId()));
        }

        Task newTask = Task.builder()
                .title(cmd.title())
                .description(cmd.description())
                .deadline(cmd.deadline())
                .priority(cmd.priority())
                .author(author)
                .category(category)
                .completed(false)
                .createdAt(LocalDateTime.now())
                .build();

        return taskRepository.save(newTask);
    }

    public Task edit(EditTaskCommand cmd, Long id, User userLogueado) throws AccessDeniedException {
        Task task = findById(id);

        if (!task.getAuthor().getId().equals(userLogueado.getId())) {
            throw new AccessDeniedException("No tienes permiso para editar esta tarea.");
        }

        Category category = null;
        if (cmd.categoryId() != null) {
            category = categoryRepository.findById(cmd.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(cmd.categoryId()));
        }

        task.setTitle(cmd.title());
        task.setDescription(cmd.description());
        task.setDeadline(cmd.deadline());
        task.setPriority(cmd.priority());
        task.setCategory(category);
        if (cmd.completed() != null) {
            task.setCompleted(cmd.completed());
        }

        return taskRepository.save(task);
    }

    public void delete(Long id, User userLogueado) throws AccessDeniedException {
        Task task = findById(id);
        if (!task.getAuthor().getId().equals(userLogueado.getId())) {
            throw new AccessDeniedException("No tienes permiso para borrar esta tarea.");
        }
        taskRepository.delete(task);
    }

    public Task addTagToTask(Long taskId, Long tagId, User userLogueado) throws AccessDeniedException {
        Task task = findById(taskId);
        
        boolean esDueño = task.getAuthor().getUsername().equals(userLogueado.getUsername());
        boolean esAdmin = userLogueado.getAuthorities().stream()
                              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esDueño && !esAdmin) {
            throw new AccessDeniedException("No tienes permiso sobre esta tarea.");
        }
        
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
        if (task.getTags().contains(tag)) {
            throw new BusinessRuleException("Esta tarea ya tiene asignada la etiqueta: " + tag.getName());
        }
        
        task.getTags().add(tag);
        return taskRepository.save(task);
    }

    public Task removeTagFromTask(Long taskId, Long tagId, User userLogueado) throws AccessDeniedException {
        Task task = findById(taskId);
        
        boolean esDueño = task.getAuthor().getUsername().equals(userLogueado.getUsername());
        boolean esAdmin = userLogueado.getAuthorities().stream()
                              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esDueño && !esAdmin) {
            throw new AccessDeniedException("No tienes permiso sobre esta tarea.");
        }
        
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
        if (!task.getTags().contains(tag)) {
            throw new BusinessRuleException("No se puede remover: la tarea no cuenta con la etiqueta '" + tag.getName() + "'.");
        }
        
        task.getTags().remove(tag);
        return taskRepository.save(task);
    }
    
    @Transactional(readOnly = true)
    public List<Task> buscarTareasPorEtiquetas(List<Long> tagIds, User userLogueado) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }
        return taskRepository.findByAuthorAndTags(userLogueado.getUsername(), tagIds);
    }
    
    @Transactional(readOnly = true)
    public TaskDashboardDto obtenerDashboardUsuario(User userLogueado) {
        List<Task> misTareas = taskRepository.findByAuthor(userLogueado);

        if (misTareas == null || misTareas.isEmpty()) {
            return TaskDashboardDto.builder()
                    .totalTasks(0)
                    .completedTasks(0)
                    .pendingTasks(0)
                    .highPriorityTasks(0)
                    .build();
        }

        long total = misTareas.size();
        long completadas = misTareas.stream().filter(Task::isCompleted).count();
        long pendientes = total - completadas;
        long altaPrioridad = misTareas.stream()
                .filter(t -> t.getPriority() != null && t.getPriority().name().equals("ALTA"))
                .count();

        return TaskDashboardDto.builder()
                .totalTasks(total)
                .completedTasks(completadas)
                .pendingTasks(pendientes)
                .highPriorityTasks(altaPrioridad)
                .build();
    }
}