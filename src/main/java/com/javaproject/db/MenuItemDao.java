package com.javaproject.db;

import com.javaproject.model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MenuItemDao {

    public List<MenuItem> listActive() throws SQLException {
        String sql = "SELECT name, description, price, category, label, rating, chef_special FROM menu_items WHERE active = TRUE ORDER BY id";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<MenuItem> items = new ArrayList<>();
            while (rs.next()) {
                items.add(new MenuItem(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("label"),
                        rs.getDouble("rating"),
                        rs.getBoolean("chef_special")
                ));
            }
            return items;
        }
    }

    public boolean isEmpty() throws SQLException {
        String sql = "SELECT 1 FROM menu_items LIMIT 1";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return !rs.next();
        }
    }

    public void seed(List<MenuItem> items) throws SQLException {
        String sql = "INSERT INTO menu_items (name, description, price, category, label, rating, chef_special) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (name) DO NOTHING";

        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (MenuItem i : items) {
                ps.setString(1, i.getName());
                ps.setString(2, i.getDescription());
                ps.setBigDecimal(3, java.math.BigDecimal.valueOf(i.getPrice()));
                ps.setString(4, i.getCategory());
                ps.setString(5, i.getLabel());
                ps.setBigDecimal(6, java.math.BigDecimal.valueOf(i.getRating()));
                ps.setBoolean(7, i.isChefSpecial());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
