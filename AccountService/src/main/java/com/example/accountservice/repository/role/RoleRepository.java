package com.example.accountservice.repository.role;

import com.example.accountservice.model.entity.Department;
import com.example.accountservice.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsById(Long id);

    List<Role> findByNameIn(List<String> names);
}
