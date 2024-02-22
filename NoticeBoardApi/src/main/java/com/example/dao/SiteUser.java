package com.example.dao;

import com.example.dto.DateInfo;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "siteUser")
@Getter
public class SiteUser extends DateInfo {
    @Id
    private String id;
    private String name;
    private String userId;
    private String password;
    private String email;

    public SiteUser(String name, String userId, String password, String email) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.email = email;
    }
}
