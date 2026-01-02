package com.javaproject.auth;

import java.util.Objects;

public final class User {
    private final String username;
    private final Role role;

    public User(String username, Role role) {
        this.username = Objects.requireNonNull(username, "username");
        this.role = Objects.requireNonNull(role, "role");
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}
