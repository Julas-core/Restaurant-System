package com.javaproject.db;

import com.javaproject.auth.Role;
import com.javaproject.auth.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

public final class UserDao {

    public Optional<UserRecord> findUserRecordByUsername(String username) throws SQLException {
        String u = normalize(username);
        if (u.isBlank()) {
            return Optional.empty();
        }

        String sql = "SELECT id, username, password_salt, password_hash, password_iterations, role FROM users WHERE username = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(new UserRecord(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getBytes("password_salt"),
                        rs.getBytes("password_hash"),
                        rs.getInt("password_iterations"),
                        Role.valueOf(rs.getString("role"))
                ));
            }
        }
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        return findUserRecordByUsername(username).map(r -> new User(r.username(), r.role()));
    }

    public User createUser(String username, char[] password, Role role) throws SQLException {
        String u = normalize(username);
        if (u.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password is required");
        }

        Role r = role == null ? Role.USER : role;
        PasswordHasher.PasswordHash ph = PasswordHasher.hash(password);

        String sql = "INSERT INTO users (username, password_salt, password_hash, password_iterations, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u);
            ps.setBytes(2, ph.salt());
            ps.setBytes(3, ph.hash());
            ps.setInt(4, ph.iterations());
            ps.setString(5, r.name());
            ps.executeUpdate();
        }

        return new User(u, r);
    }

    public Optional<User> authenticate(String username, char[] password) throws SQLException {
        Optional<UserRecord> recOpt = findUserRecordByUsername(username);
        if (recOpt.isEmpty()) {
            return Optional.empty();
        }

        UserRecord rec = recOpt.get();
        PasswordHasher.PasswordHash stored = new PasswordHasher.PasswordHash(rec.salt(), rec.hash(), rec.iterations());
        if (!PasswordHasher.verify(password, stored)) {
            return Optional.empty();
        }

        return Optional.of(new User(rec.username(), rec.role()));
    }

    public void ensureSeedUser(String username, String password, Role role) throws SQLException {
        if (findByUsername(username).isPresent()) {
            return;
        }
        createUser(username, password == null ? new char[0] : password.toCharArray(), role);
    }

    private String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }

    public record UserRecord(long id, String username, byte[] salt, byte[] hash, int iterations, Role role) {
    }
}
