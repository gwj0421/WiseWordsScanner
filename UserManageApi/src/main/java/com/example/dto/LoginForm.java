package com.example.dto;

import lombok.Getter;

@Getter
public class LoginForm {
    private String userId;
    private String password;

    public LoginForm(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
