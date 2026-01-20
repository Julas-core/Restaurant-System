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
        try (Connection c = Db.getConnection(); 
            PreparedStatement ps = c.prepareStatement(sql)) {
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
        return findUserRecordByUsername(username).map(r -> new User(r.id(), r.username(), r.role()));
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
        long newId = 0;
        try (Connection c = Db.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u);
            ps.setBytes(2, ph.salt());
            ps.setBytes(3, ph.hash());
            ps.setInt(4, ph.iterations());
            ps.setString(5, r.name());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    newId = rs.getLong(1);
                }
            }
        }

        return new User(newId, u, r);
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

        return Optional.of(new User(rec.id(), rec.username(), rec.role()));
    }

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection c = Db.getConnection();
             java.sql.Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updatePassword(long userId, char[] newPassword) throws SQLException {
        if (newPassword == null || newPassword.length == 0) {
            throw new IllegalArgumentException("Password is required");
        }
        PasswordHasher.PasswordHash ph = PasswordHasher.hash(newPassword);
        String sql = "UPDATE users SET password_salt=?, password_hash=?, password_iterations=? WHERE id=?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBytes(1, ph.salt());
            ps.setBytes(2, ph.hash());
            ps.setInt(3, ph.iterations());
            ps.setLong(4, userId);
            ps.executeUpdate();
        }
    }

    public java.util.List<UserRecord> getAllUsers() throws SQLException {
        java.util.List<UserRecord> list = new java.util.ArrayList<>();
        String sql = "SELECT id, username, password_salt, password_hash, password_iterations, role FROM users ORDER BY username";
        try (Connection c = Db.getConnection();
             java.sql.Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new UserRecord(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getBytes("password_salt"),
                    rs.getBytes("password_hash"),
                    rs.getInt("password_iterations"),
                    Role.valueOf(rs.getString("role"))
                ));
            }
        }
        return list;
    }

    public void updateUserRole(long userId, Role newRole) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newRole.name());
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
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
