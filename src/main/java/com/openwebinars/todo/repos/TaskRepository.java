package com.openwebinars.todo.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openwebinars.todo.model.Task;
import com.openwebinars.todo.users.User;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAuthor(User author);
    @Query("SELECT DISTINCT t FROM Task t JOIN t.tags tag WHERE t.author.username = :username AND tag.id IN :tagIds")
    List<Task> findByAuthorAndTags(@Param("username") String username, @Param("tagIds") List<Long> tagIds);

}
