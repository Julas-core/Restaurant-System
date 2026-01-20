package com.javaproject.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PromoDao {

    public Optional<Double> getDiscountPercent(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        
        String sql = "SELECT discount_percent FROM coupons WHERE code = ? AND active = TRUE";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, code.trim()); // Case-sensitive exact match for simplicity
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getDouble("discount_percent"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
