package com.example.demo.dtos;

import com.example.demo.enums.UserRole;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserRole role;
}