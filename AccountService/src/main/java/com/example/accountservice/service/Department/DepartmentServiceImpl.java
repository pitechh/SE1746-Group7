package com.example.accountservice.service.Department;

import com.example.accountservice.model.entity.Department;
import com.example.accountservice.repository.department.DepartmentRepository;
import com.example.accountservice.service.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class DepartmentServiceImpl extends BaseServiceImpl<Department, Long, DepartmentRepository>
        implements DepartmentService
{

    @Override
    public HashMap<String, Long> getDepartmentCodeIdMap() {
        List<Department> departments = (List<Department>) repository.findAll();
        HashMap<String, Long> map = new HashMap<>();
        for (Department department : departments) {
            map.put(department.getCode(), department.getId());
        }
        return map;
    }
}
