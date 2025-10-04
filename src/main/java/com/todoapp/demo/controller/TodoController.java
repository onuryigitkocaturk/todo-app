package com.todoapp.demo.controller;

import com.todoapp.demo.domain.Priority;
import com.todoapp.demo.domain.Todo;
import com.todoapp.demo.dto.TodoDto;
import com.todoapp.demo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<Page<TodoDto>> getTodos(
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Boolean completed,
            Pageable pageable,
            Authentication authentication) {

        // Debug için
        System.out.println(">>> Auth name: " + authentication.getName());
        System.out.println(">>> Principal class: " + authentication.getPrincipal().getClass());

        String username = authentication.getName();
        Page<Todo> todos = todoService.getFilteredTodos(username, priority, completed, pageable);
        Page<TodoDto> dtoPage = todos.map(todoService::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody Todo todo, Authentication authentication) {
        String username = authentication.getName();
        System.out.println(">>> Creating todo for user: " + username);

        Todo created = todoService.createTodo(todo, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.toDto(created));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoDto> completeTodo(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        System.out.println(">>> Completing todo for user: " + username);

        Todo updated = todoService.markAsCompleted(id, username);
        return ResponseEntity.ok(todoService.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        System.out.println(">>> Deleting todo for user: " + username);

        todoService.softDeleteTodo(id, username);
        return ResponseEntity.noContent().build();
    }
}
