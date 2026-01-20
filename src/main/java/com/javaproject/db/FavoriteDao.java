package com.javaproject.db;

import com.javaproject.model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDao {

    public void addFavorite(long userId, long menuItemId) throws SQLException {
        String sql = "INSERT INTO favorites (user_id, menu_item_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, menuItemId);
            ps.executeUpdate();
        }
    }

    public void removeFavorite(long userId, long menuItemId) throws SQLException {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND menu_item_id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, menuItemId);
            ps.executeUpdate();
        }
    }

    public boolean isFavorite(long userId, long menuItemId) throws SQLException {
        String sql = "SELECT 1 FROM favorites WHERE user_id = ? AND menu_item_id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, menuItemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<MenuItem> getFavorites(long userId) throws SQLException {
        String sql = "SELECT m.* FROM menu_items m JOIN favorites f ON m.id = f.menu_item_id WHERE f.user_id = ? AND m.active = TRUE";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
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
    }
}
