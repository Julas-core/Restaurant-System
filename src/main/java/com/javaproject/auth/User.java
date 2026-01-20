package com.javaproject.auth;

import java.util.Objects;

public final class User {
    private final long id;
    private final String username;
    private final Role role;

    public User(long id, String username, Role role) {
        this.id = id;
        this.username = Objects.requireNonNull(username, "username");
        this.role = Objects.requireNonNull(role, "role");
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
    
    // Legacy constructor compatibility if needed (try to avoid using this)
    public User(String username, Role role) {
        this(0, username, role);
    }
}
