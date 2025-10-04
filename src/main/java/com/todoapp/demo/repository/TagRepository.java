package com.todoapp.demo.repository;

import com.todoapp.demo.domain.Tag;
import com.todoapp.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUser(User user);
}
