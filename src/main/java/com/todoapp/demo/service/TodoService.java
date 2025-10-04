package com.todoapp.demo.service;

import com.todoapp.demo.domain.Priority;
import com.todoapp.demo.domain.Todo;
import com.todoapp.demo.domain.User;
import com.todoapp.demo.repository.TodoRepository;
import com.todoapp.demo.repository.UserRepository;
import com.todoapp.demo.spec.TodoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.todoapp.demo.dto.TodoDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public Page<Todo> getFilteredTodos(String username, Priority priority, Boolean completed, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<Todo> spec = Specification.where(TodoSpecification.belongsToUser(user));

        if (priority != null) {
            spec = spec.and(TodoSpecification.hasPriority(priority));
        }

        if (completed != null) {
            spec = spec.and(TodoSpecification.isCompleted(completed));
        }

        return todoRepository.findAll(spec, pageable);
    }

    public TodoDto toDto(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setDueDate(todo.getDueDate());
        dto.setPriority(todo.getPriority());
        dto.setCompleted(todo.isCompleted());

        // ✅ Tag isimlerini set et
        Set<String> tagNames = todo.getTags()
                .stream()
                .map(tag -> tag.getName())
                .collect(Collectors.toSet());
        dto.setTags(tagNames);

        return dto;
    }

    public Todo createTodo(Todo todo, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    public List<Todo> getTodosByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return todoRepository.findAll((root, query, cb) ->
                cb.equal(root.get("user"), user)
        );
    }

    public Todo markAsCompleted(Long todoId, String username) {
        Todo todo = getUserTodoById(todoId, username);
        todo.setCompleted(true);
        return todoRepository.save(todo);
    }

    public void softDeleteTodo(Long todoId, String username) {
        Todo todo = getUserTodoById(todoId, username);
        todoRepository.delete(todo); // Soft delete aktif, gerçekten silmez
    }

    private Todo getUserTodoById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return todoRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Todo not found or unauthorized"));
    }
}
