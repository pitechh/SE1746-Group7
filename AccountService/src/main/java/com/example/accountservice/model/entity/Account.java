package com.example.accountservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {
    @Column(name = "code")
    private String code;

    @Column(name = "email")
    private String email;

    @Column(name = "personal_email")
    private String personalEmail;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "password")
    private String password;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "password_salt")
    private String passwordSalt;

    @Column(name = "phone")
    private String phone;

    @Column(name = "salary", nullable = false, precision = 18, scale = 2)
    private BigDecimal salary = BigDecimal.ZERO;

    @Column(name = "image")
    private String image;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
