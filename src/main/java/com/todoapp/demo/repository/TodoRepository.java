package com.todoapp.demo.repository;

import com.todoapp.demo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {
    // İleride filtreleme, sıralama için Specification kullanacağız
}
