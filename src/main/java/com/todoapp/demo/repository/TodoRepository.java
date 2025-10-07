package com.todoapp.demo.repository;

import com.todoapp.demo.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

// @Repository anotasyonu bu interface'in bir Spring Data repository olduğunu belirtir
// spring, bu sınıfı otomatik olarak algılar ve gerekli bean'i oluşturur.

// JpaRepository -> CRUD işlemleri
// JpaSpecificationExecutor -> dinamik sorgular
// @EntityGraph -> ilişkili entityleri eager yükleme
// Pageable/Page -> sayfalama, api yanıtlarını verimli hale getirme


@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {

    // normalde  JPA ilişki tabloları lazy(tembel) olarak yükler.
    // Todo çektiğinde tags ilişkisi hemen yüklenmez - ilk erişimde veritabanına ek
    // sorgu atılır. (N+1) problemi -> araştır - sor
    @Override
    @EntityGraph(attributePaths = {"tags"})
    Page<Todo> findAll(Specification<Todo> spec, Pageable pageable);
}
