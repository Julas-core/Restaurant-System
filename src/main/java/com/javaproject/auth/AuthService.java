package com.javaproject.auth;

import com.javaproject.db.ProfileDao;
import com.javaproject.db.SchemaInitializer;
import com.javaproject.db.UserDao;

import java.sql.SQLException;
import java.util.Optional;

public final class AuthService {
    private static final AuthService INSTANCE = new AuthService();

    private final UserDao userDao = new UserDao();
    private final ProfileDao profileDao = new ProfileDao();

    private AuthService() {
        // Ensure schema exists + seed demo users when DB is available.
        try {
            SchemaInitializer.init();
            userDao.ensureSeedUser("admin", "admin123", Role.ADMIN);
            userDao.ensureSeedUser("user", "user123", Role.USER);
            profileDao.ensureDefaultProfile("admin");
            profileDao.ensureDefaultProfile("user");
        } catch (SQLException ignored) {
            // If DB isn't reachable yet, UI can still load; login/register will show errors.
        }
    }

    public static AuthService getInstance() {
        return INSTANCE;
    }

    public Optional<User> login(String username, String password) {
        try {
            return userDao.authenticate(username, password == null ? new char[0] : password.toCharArray());
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public User register(String username, String password, Role role) {
        try {
            SchemaInitializer.init();
            User created = userDao.createUser(username, password == null ? new char[0] : password.toCharArray(), role);
            profileDao.ensureDefaultProfile(created.getUsername());
            return created;
        } catch (SQLException e) {
            throw new IllegalArgumentException("Database unavailable or username already exists");
        }
    }
}
