package com.lyra.caps.model.enums;

public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    MODERATOR("ROLE_MODERATOR"),
    EDITOR("ROLE_EDITOR"),
    GUEST("ROLE_GUEST");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getAuthority() {
        return role;
    }
}
