package com.procura.department.repo;

import com.procura.department.domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByNameAndIsDeletedFalse(String name);
    Page<Department> findByIsDeletedFalse(Pageable pageable);
    List<Department> findAllByIsDeletedFalse();
    boolean existsByNameAndIsDeletedFalse(String name);
}
