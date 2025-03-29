package com.example.accountservice.service.role;

import com.example.accountservice.model.entity.Role;
import com.example.accountservice.repository.role.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService{
    private RoleRepository roleRepository;

    @Override
    public boolean exists(long id) {
        return roleRepository.existsById(id);
    }

    @Override
    public HashMap<String, Long> getRoleNameIdMap() {
        List<Role> roles = roleRepository.findAll();
        HashMap<String, Long> map = new HashMap<>();
        for (Role role : roles) {
            map.put(role.getName(), role.getId());
        }
        return map;
    }
}
