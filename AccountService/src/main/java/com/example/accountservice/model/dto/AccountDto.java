package com.example.accountservice.model.dto;

import com.example.accountservice.model.entity.Department;
import com.example.accountservice.model.entity.Role;
import com.poiji.annotation.ExcelCellName;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private long id;

    private String code;

    private String email;

    @ExcelCellName("personal_email")
    @NotNull
    private String personalEmail;

    @ExcelCellName("fullname")
    @NotNull
    private String fullname;

    private String password;

    @ExcelCellName("phone")
    @NotNull
    private String phone;

    @ExcelCellName("salary")
    private BigDecimal salary = BigDecimal.ZERO;

    @ExcelCellName("image")
    private String image;

    private Long roleId = 1L;

    @ExcelCellName("role")
    private String roleName = "Employee";

    private Role role;

    @NonNull
    private Long departmentId;

    @ExcelCellName("department")
    private String departmentCode;

    private Department department;

    private LocalDateTime createdDate;
}
