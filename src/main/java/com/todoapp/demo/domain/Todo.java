package com.todoapp.demo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
// buradaki sqldelete softdelete özelliğini aktif hale getiriyor. Hibernate'e diyoruz ki
// veritabanından satırı tamamen silme, sadece deleted kolonunu true yap
// where deleted = false koşulu sayesinde, deleted = true olan kayıtlar findAll(),
// findById() sonuçlarına dahil olmazlar.

@Entity
@Table(name = "todos")
@SQLDelete(sql = "UPDATE todos SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // string için JPA default olarak VARCHAR(256) yer ayırır,
    // title için bu yeterli geldi, ama description'da "text" oldugunu belirttik
    // böylece description neredeyse sınırsız yer ayırıyor.
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    // JSON "LocalDate" tipini tanımaz, bu yüzden nesneyi stringe çevirmek lazım
    // bunu da @JsonFormat anotasyonuyla yaparız.
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    // java tarafında todo.getPriority() çağırıldığında  bir enum
    // nesnesi dönüyor. string olmasını beklerdik, çevirme yapılıyor
    // priority gönderilmemişse default olarak medium ayarlanıyor.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false)
    private boolean deleted = false;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    // buradaki optional = false, bu ilişkinin zorunlu olduğunu anlatıyor, bir todo
    // bir user'a ait olmak zorunda
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // bir todo birden fazla tag alabilir, bir tag birden fazla todo'ya eklenebilir
    // @JoinTable diyor ki aradaki ilişkiyi "todo_tags" isimli join tablosunda tut
    // joinColumns : köprü tablosunda Todo'nun FK kolonu (todo_id)
    // inverseJoinColumns : köprü tablosunda Tag'in FK kolonu (tag_id)
    // todo_tags tablosundaki her satır "şu todo <-> şu tag" eşleşmesini temsil eder
    // FK'lar, boşluğa işaret eden ilişki olmasın diye DB seviyesinde güvence sağlar.
    @ManyToMany
    @JoinTable(
            name = "todo_tags",
            joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // --- Constructors ---
    // JPA sadece boş constructor görse yeter
    // JPA parametreli constructor'ı hiç kullanmaz, Todo'yu veritabanından çekerken
    // default constructor ile oluşturur, sonra field'ları tek tek set eder.
    public Todo() {
    }

    public Todo(String title, String description, LocalDate dueDate, Priority priority,
                boolean completed, User user, Set<Tag> tags) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
        this.user = user;
        this.tags = tags;
    }

    // --- Getters & Setters ---
    // JPA entity'ler alanlara doğrudan erişebilir
    // bu yüzden getter&setterlar JPA için zorunlu değil
    // ama kod seviyesinde controller, service, dto, json serializer bu kodu kullanıyor
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", completed=" + completed +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
