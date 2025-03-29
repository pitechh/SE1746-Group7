package com.example.accountservice.repository.department;

import com.example.accountservice.model.entity.Department;
import com.example.accountservice.repository.BaseRepository;

import java.util.List;

public interface DepartmentRepository extends BaseRepository<Department, Long> {
    // Thêm hàm này
    List<Department> findByCodeIn(List<String> codes);
}
