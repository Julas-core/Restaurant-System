package com.javaproject.db;

import com.javaproject.ProfileData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class ProfileDao {

    public Optional<ProfileData> findByUsername(String username) throws SQLException {
        String sql = "SELECT p.first_name, p.last_name, p.email, p.phone, p.addr_home, p.addr_work " +
                "FROM profiles p JOIN users u ON u.id = p.user_id WHERE u.username = ?";

        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new ProfileData(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("addr_home"),
                        rs.getString("addr_work")
                ));
            }
        }
    }

    public void upsertByUsername(String username, ProfileData profile) throws SQLException {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, email, phone, addr_home, addr_work) " +
                "SELECT u.id, ?, ?, ?, ?, ?, ? FROM users u WHERE u.username = ? " +
                "ON CONFLICT (user_id) DO UPDATE SET " +
                "first_name = EXCLUDED.first_name, " +
                "last_name = EXCLUDED.last_name, " +
                "email = EXCLUDED.email, " +
                "phone = EXCLUDED.phone, " +
                "addr_home = EXCLUDED.addr_home, " +
                "addr_work = EXCLUDED.addr_work";

        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nullToEmpty(profile.getFirstName()));
            ps.setString(2, nullToEmpty(profile.getLastName()));
            ps.setString(3, nullToEmpty(profile.getEmail()));
            ps.setString(4, nullToEmpty(profile.getPhone()));
            ps.setString(5, nullToEmpty(profile.getAddrHome()));
            ps.setString(6, nullToEmpty(profile.getAddrWork()));
            ps.setString(7, username);
            ps.executeUpdate();
        }
    }

    public void ensureDefaultProfile(String username) throws SQLException {
        ProfileData defaults = new ProfileData("", "", username + "@example.com", "", "123 Main St, Apt 4B", "456 Corporate Blvd, Suite 200");
        upsertByUsername(username, defaults);
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
