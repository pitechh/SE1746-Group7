package com.example.accountservice.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String code;

    private String email;

    private String personalEmail;

    private String fullname;

    private String password;

    private String phone;

    private BigDecimal salary;

    private String image;

    private Long roleId;

    private Long departmentId;
}
