package com.procura.user.repo;

import com.procura.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByEmail(String email);
    boolean existsByEmailAndIsDeletedFalse(String email);
    Page<User> findByIsDeletedFalse(Pageable pageable);
}
