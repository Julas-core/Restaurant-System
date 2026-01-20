package com.javaproject.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfigDao {

    public String getValue(String key, String defaultValue) {
        String sql = "SELECT value FROM system_config WHERE key = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public void setValue(String key, String value) {
        // Upsert logic (Postgres specific ON CONFLICT)
        String sql = "INSERT INTO system_config (key, value) VALUES (?, ?) " +
                     "ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(getValue(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
