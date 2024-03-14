package com.example.dto;

import lombok.Getter;

@Getter
public enum RoleType {
    VISITOR("ROLE_VISITOR"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String role;

    RoleType(String role) {
        this.role = role;
    }

    public static RoleType getRoleTypeByRole(String role) {
        for (RoleType roleType : values()) {
            if (roleType.getRole().equals(role)) {
                return roleType;
            }
        }
        return RoleType.VISITOR;
    }

    public static boolean isMatch(RoleType base, String target) {
        return getRoleTypeByRole(target).equals(base);
    }
}
