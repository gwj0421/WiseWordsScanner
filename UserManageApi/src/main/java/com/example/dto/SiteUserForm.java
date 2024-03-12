package com.example.dto;

import com.example.dao.SiteUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteUserForm {
    private String name;
    private String userId;
    private String password;
    private String email;

    public SiteUserForm(String name, String userId, String password, String email) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.email = email;
    }

    public SiteUser toSiteUser() {
        return new SiteUser(getName(), getUserId(), getPassword(), getEmail());
    }
}
