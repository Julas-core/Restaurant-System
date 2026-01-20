package com.javaproject.db;

import com.javaproject.model.Feedback;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDao {

    public void addFeedback(long userId, int rating, String comment) {
        addFeedback(userId, rating, comment, null);
    }

    public void addFeedback(long userId, int rating, String comment, Long menuItemId) {
        addFeedback(userId, rating, comment, menuItemId, null);
    }
    
    public void addFeedback(long userId, int rating, String comment, Long menuItemId, String tags) {
        String sql = "INSERT INTO feedback (user_id, rating, comment, menu_item_id, tags) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, rating);
            ps.setString(3, comment);
            if (menuItemId == null) {
                ps.setObject(4, null);
            } else {
                ps.setLong(4, menuItemId);
            }
            if (tags == null) {
                ps.setObject(5, null);
            } else {
                ps.setString(5, tags);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getAverageRating() {
        String sql = "SELECT AVG(rating) FROM feedback";
        try (Connection c = Db.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) {
                 double val = rs.getDouble(1);
                 if (rs.wasNull()) return 0.0;
                 return val;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<Feedback> getAllFeedback() {
        List<Feedback> list = new ArrayList<>();
        // Join with users table to get the username, and menu_items for food name
        String sql = "SELECT f.id, f.user_id, u.username, f.rating, f.comment, f.created_at, f.admin_reply, f.archived, f.menu_item_id, f.tags, m.name as item_name " +
                     "FROM feedback f JOIN users u ON f.user_id = u.id " +
                     "LEFT JOIN menu_items m ON f.menu_item_id = m.id " +
                     "WHERE f.archived = FALSE " +
                     "ORDER BY f.created_at DESC";
        
        try (Connection c = Db.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Long mId = rs.getLong("menu_item_id");
                if (rs.wasNull()) mId = null;
                
                list.add(new Feedback(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getString("username"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("created_at"),
                    rs.getString("admin_reply"),
                    rs.getBoolean("archived"),
                    mId,
                    rs.getString("item_name"),
                    rs.getString("tags")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public int getUnreadFeedbackCount() {
        String sql = "SELECT COUNT(*) FROM feedback WHERE admin_reply IS NULL";
        try (Connection c = Db.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalFeedbackCount() {
        String sql = "SELECT COUNT(*) FROM feedback";
        try (Connection c = Db.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public void replyToFeedback(long feedbackId, String reply) {
        String sql = "UPDATE feedback SET admin_reply = ? WHERE id = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, reply);
            ps.setLong(2, feedbackId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void archiveFeedback(long feedbackId) {
        String sql = "UPDATE feedback SET archived = TRUE WHERE id = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, feedbackId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
