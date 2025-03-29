package com.example.accountservice.service.role;

import java.util.HashMap;
import java.util.List;

public interface RoleService {
    boolean exists(long id);

    HashMap<String, Long> getRoleNameIdMap();
}
