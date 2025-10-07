package com.todoapp.demo.repository;

import com.todoapp.demo.domain.Tag;
import com.todoapp.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
// bu repository, veritabanındaki tüm Tag kayıtlarını yönetir
// ve özellikle belirli bir kullanıcıya ait tag'leri bulmak için kullanılır.
// User nesnesine göre filtreleme yapar, böylece herkes kendi tag'lerini görebilir.
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUser(User user);
}
