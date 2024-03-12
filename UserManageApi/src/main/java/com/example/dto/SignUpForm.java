package com.example.dto;

import lombok.Getter;

@Getter
public class SignUpForm {
    private String name;
    private String userId;
    private String password1;
    private String password2;
    private String email;

    public SignUpForm(String name, String userId, String password1, String password2, String email) {
        this.name = name;
        this.userId = userId;
        this.password1 = password1;
        this.password2 = password2;
        this.email = email;
    }
}
