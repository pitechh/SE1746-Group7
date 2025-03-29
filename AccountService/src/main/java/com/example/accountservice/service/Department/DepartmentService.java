package com.example.accountservice.service.Department;

import com.example.accountservice.model.entity.Department;
import com.example.accountservice.service.BaseService;

import java.util.HashMap;
import java.util.List;

public interface DepartmentService extends BaseService<Department, Long> {
    HashMap<String, Long> getDepartmentCodeIdMap();
}
