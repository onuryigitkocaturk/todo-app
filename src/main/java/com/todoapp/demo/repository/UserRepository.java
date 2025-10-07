package com.todoapp.demo.repository;

import com.todoapp.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
// bu repository AuthService ve UserService içinde, kayıt olunurken çağrılır
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
