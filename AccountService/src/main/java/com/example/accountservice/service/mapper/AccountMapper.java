package com.example.accountservice.service.mapper;

import com.example.accountservice.model.dto.AccountDto;
import com.example.accountservice.model.entity.Account;
import com.example.accountservice.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.name", target = "roleName")
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.code", target = "departmentCode")
    AccountDto toDTO(Account account);

    @Mapping(source = "roleId", target = "role.id")
    @Mapping(source = "departmentId", target = "department.id")
    Account toEntity(AccountDto accountDto);

    RegisterRequest toRegisterRequest(AccountDto accountDto);
}
