package com.openwebinars.todo.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.openwebinars.todo.users.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity


public class Task {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String title;

    @Lob
    private String description;
    
    private boolean completed;

    private LocalDateTime deadline;
    
    private TaskPriority priority;

    @ManyToOne
    @JoinColumn(name = "user_id") 
    private User author;

    @ManyToOne
    @JoinColumn(name = "category_id") 
    private Category category;

 // En com.openwebinars.todo.model.Task.java

    @ManyToMany
    @JoinTable(
        name = "task_tags",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "tags_id")
    )
    @Builder.Default // <--- IMPORTANTE si usas @Builder
    private Set<Tag> tags = new HashSet<>(); // <--- ¡AÑADE ESTO AQUÍ!
    //He decidido poner un hashSet aqui para controlar mejor las etiqutas duplicadas ya que habra muchas etiquetas y podremos controlar que el nombre de la etiqueta este duplicado gracias al HashSet

}
