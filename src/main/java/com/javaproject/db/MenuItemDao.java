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
        String sql = "SELECT id, name, description, price, category, label, rating, chef_special, image_path FROM menu_items WHERE active = TRUE ORDER BY id";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<MenuItem> items = new ArrayList<>();
            while (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("label"),
                        rs.getDouble("rating"),
                        rs.getBoolean("chef_special"),
                        rs.getString("image_path")
                );
                item.setId(rs.getLong("id"));
                items.add(item);
            }
            return items;
        }
    }

    public void add(MenuItem item) throws SQLException {
        String sql = "INSERT INTO menu_items (name, description, price, category, label, rating, chef_special, image_path, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUE)";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(item.getPrice()));
            ps.setString(4, item.getCategory());
            ps.setString(5, item.getLabel());
            ps.setBigDecimal(6, java.math.BigDecimal.valueOf(item.getRating()));
            ps.setBoolean(7, item.isChefSpecial());
            ps.setString(8, item.getImagePath());
            ps.executeUpdate();
        }
    }

    public void deleteByName(String name) throws SQLException {
        String sql = "UPDATE menu_items SET active = FALSE WHERE name = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    public void updateByName(String oldName, MenuItem item) throws SQLException {
        String sql = "UPDATE menu_items SET name=?, description=?, price=?, category=?, label=?, chef_special=?, image_path=? WHERE name=?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(item.getPrice()));
            ps.setString(4, item.getCategory());
            ps.setString(5, item.getLabel());
            ps.setBoolean(6, item.isChefSpecial());
            ps.setString(7, item.getImagePath());
            ps.setString(8, oldName);
            ps.executeUpdate();
        }
    }

    public boolean isEmpty() throws SQLException {
        String sql = "SELECT 1 FROM menu_items LIMIT 1";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return !rs.next();
        }
    }

    public void seed(List<MenuItem> items) throws SQLException {
        String sql = "INSERT INTO menu_items (name, description, price, category, label, rating, chef_special, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
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
                ps.setString(8, i.getImagePath());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
