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

// @Service annotasyonu bu sınıfın uygulama mantığını içerdiğini Spring'e söyler.
// Spring uygulamayı başlatırken bütün sınıfları tarar,
// @Service, @Component, @Repository, @Controller görürse
// bu sınıfı Spring konteynırına ekler.
// böylece bu sınıf , proje genelinde otomatik olarak enjekte edilebilir hale gelir.
// @Service -> bu sınıf uygulamanın iş mantığını yönetiyor, bean olarak kaydet.
// "bean" -> Spring'in yönetimi altındaki bir nesnedir
// Spring, o sınıfın yaşam döngüsünü yönetir:
// uygulama başlatırken oluşturur, dependency injection yapar, uygulama kapanınca yok eder

@Service
public class TodoService {

    // veritabanıyla konuşmak için kullanılan repository katmanları:
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }
    // kullanıcıya ait todoları getirir, filtreler uygulanır, sonuçlar
    // pageable formatında döner
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
    // entity -> dto dönüşümü burada
    // methodun amacı entity'yi dış dünyada dönerken dto formatına dönüştürmektir
    // (yani JSON'a uygun sade bir veri)
    public TodoDto toDto(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setDueDate(todo.getDueDate());
        dto.setPriority(todo.getPriority());
        dto.setCompleted(todo.isCompleted());

        // tag isimlerinin set ediliş
        Set<String> tagNames = todo.getTags()
                .stream()
                .map(tag -> tag.getName())
                .collect(Collectors.toSet());
        dto.setTags(tagNames);

        return dto;
    }
    // controller'dan gelen Todo nesnesini alır, hangi kullanıcıya aitse
    // o kullanıcıyla ilişkilendirilir.
    // ardından Repository'den gelen save methodu ile kaydeder.
    public Todo createTodo(Todo todo, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        todo.setUser(user);
        return todoRepository.save(todo);
    }
    // kullanıcıya ait tüm todoları döndürür,
    public List<Todo> getTodosByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return todoRepository.findAll((root, query, cb) ->
                cb.equal(root.get("user"), user)
        );
    }
    // getUserTodoById() metodunu çağırarak güvenlik kontrolü yapar
    // eğer kendi todosu ise tamamlandı (true) olarak işaretler. kaydedip döner
    // PATCH /todos/{id}/complete endpoint'inin arkasındaki servistir.
    public Todo markAsCompleted(Long todoId, String username) {
        Todo todo = getUserTodoById(todoId, username);
        todo.setCompleted(true);
        return todoRepository.save(todo);
    }
    // getUserTodoById() ile güvenlik kontrolü yapar.
    // ardından silme işlemini yapar, fakat veritabanından fiziksel olarak silinmez
    // Entity'deki @SqlDelete ya da deleted sütünunu soft delete olarak işaretler.
    public void softDeleteTodo(Long todoId, String username) {
        Todo todo = getUserTodoById(todoId, username);
        todoRepository.delete(todo); // Soft delete aktif, gerçekten silmez
    }
    // Todo ID'ye göre todo bulunur
    // kullanıcıya ait olup olmadığı kontrol edilir
    // eğer değilse RuntimeException("Todo not found or unauthorized") döner
    private Todo getUserTodoById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return todoRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Todo not found or unauthorized"));
    }
}
