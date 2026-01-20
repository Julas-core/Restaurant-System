package com.javaproject.db;

import com.javaproject.auth.User;
import com.javaproject.model.CartLine;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class OrderDao {

    private final UserDao userDao = new UserDao();

    public long createOrder(
            Optional<User> user,
            List<CartLine> cartLines,
            boolean delivery,
            String promoCode,
            double subtotal,
            double tax,
            double deliveryFee,
            double discount,
            double total
    ) throws SQLException {
        if (cartLines == null || cartLines.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        try (Connection c = Db.getConnection()) {
            c.setAutoCommit(false);
            try {
                Long userId = null;
                if (user != null && user.isPresent()) {
                    userId = userDao.findUserRecordByUsername(user.get().getUsername()).map(UserDao.UserRecord::id).orElse(null);
                }

                long orderId = insertOrder(c, userId, delivery, promoCode, subtotal, tax, deliveryFee, discount, total);
                insertOrderLines(c, orderId, cartLines);

                c.commit();
                return orderId;
            } catch (Exception e) {
                try {
                    c.rollback();
                } catch (SQLException ignored) {
                }
                if (e instanceof SQLException se) {
                    throw se;
                }
                if (e instanceof RuntimeException re) {
                    throw re;
                }
                throw new SQLException("Failed to create order", e);
            } finally {
                try {
                    c.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public List<OrderSummary> listRecent(int limit) throws SQLException {
        int safeLimit = Math.max(1, Math.min(limit, 200));

        String sql = "SELECT o.id, o.status, o.total, u.username, p.first_name, p.last_name, " +
                "(SELECT string_agg((ol.quantity::text || 'x ' || ol.item_name), ', ' ORDER BY ol.id) FROM order_lines ol WHERE ol.order_id = o.id) AS items " +
                "FROM orders o " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "LEFT JOIN profiles p ON u.id = p.user_id " +
                "ORDER BY o.placed_at DESC " +
                "LIMIT ?";

        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, safeLimit);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderSummary> out = new ArrayList<>();
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String status = rs.getString("status");
                    BigDecimal total = rs.getBigDecimal("total");

                    String username = rs.getString("username");
                    String first = rs.getString("first_name");
                    String last = rs.getString("last_name");
                    String items = rs.getString("items");

                    out.add(new OrderSummary(id, status, username, first, last, items == null ? "" : items, total));
                }
                return out;
            
            }
        }
    }

    public List<OrderSummary> searchOrders(String query, String statusFilter) {
        List<OrderSummary> out = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT o.id, o.status, o.total, u.username, p.first_name, p.last_name, " +
            "(SELECT string_agg((ol.quantity::text || 'x ' || ol.item_name), ', ' ORDER BY ol.id) FROM order_lines ol WHERE ol.order_id = o.id) AS items " +
            "FROM orders o " +
            "LEFT JOIN users u ON o.user_id = u.id " +
            "LEFT JOIN profiles p ON u.id = p.user_id " +
            "WHERE 1=1 "
        );

        if (query != null && !query.isBlank()) {
            sql.append("AND (CAST(o.id AS TEXT) LIKE ? OR u.username ILIKE ? OR p.first_name ILIKE ?) ");
        }
        if (statusFilter != null && !statusFilter.equals("ALL")) {
            sql.append("AND o.status = ? ");
        }

        sql.append("ORDER BY o.placed_at DESC LIMIT 50");

        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int idx = 1;
            if (query != null && !query.isBlank()) {
                String q = "%" + query.trim() + "%";
                ps.setString(idx++, q);
                ps.setString(idx++, q);
                ps.setString(idx++, q);
            }
            if (statusFilter != null && !statusFilter.equals("ALL")) {
                ps.setString(idx++, statusFilter);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String status = rs.getString("status");
                    BigDecimal total = rs.getBigDecimal("total");
                    String username = rs.getString("username");
                    String first = rs.getString("first_name");
                    String last = rs.getString("last_name");
                    String items = rs.getString("items");

                    out.add(new OrderSummary(id, status, username, first, last, items == null ? "" : items, total));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    public void updateStatus(long orderId, String newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, orderId);
            ps.executeUpdate();
        }
    }

    private long insertOrder(
            Connection c,
            Long userId,
            boolean delivery,
            String promoCode,
            double subtotal,
            double tax,
            double deliveryFee,
            double discount,
            double total
    ) throws SQLException {
        String sql = "INSERT INTO orders (user_id, delivery, promo_code, subtotal, tax, delivery_fee, discount, total) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (userId == null) {
                ps.setObject(1, null);
            } else {
                ps.setLong(1, userId);
            }
            ps.setBoolean(2, delivery);
            if (promoCode == null || promoCode.isBlank()) {
                ps.setObject(3, null);
            } else {
                ps.setString(3, promoCode.trim());
            }
            ps.setBigDecimal(4, BigDecimal.valueOf(subtotal));
            ps.setBigDecimal(5, BigDecimal.valueOf(tax));
            ps.setBigDecimal(6, BigDecimal.valueOf(deliveryFee));
            ps.setBigDecimal(7, BigDecimal.valueOf(discount));
            ps.setBigDecimal(8, BigDecimal.valueOf(total));

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Failed to insert order");
                }
                return rs.getLong(1);
            }
        }
    }

    private void insertOrderLines(Connection c, long orderId, List<CartLine> cartLines) throws SQLException {
        String sql = "INSERT INTO order_lines (order_id, menu_item_id, item_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (CartLine line : cartLines) {
                if (line == null || line.getItem() == null) {
                    continue;
                }

                String itemName = line.getItem().getName();
                BigDecimal unitPrice = BigDecimal.valueOf(line.getItem().getPrice());
                int qty = Math.max(1, line.getQuantity());

                Long menuItemId = findMenuItemIdByName(c, itemName);

                ps.setLong(1, orderId);
                if (menuItemId == null) {
                    ps.setObject(2, null);
                } else {
                    ps.setLong(2, menuItemId);
                }
                ps.setString(3, itemName);
                ps.setBigDecimal(4, unitPrice);
                ps.setInt(5, qty);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private Long findMenuItemIdByName(Connection c, String name) throws SQLException {
        if (name == null || name.isBlank()) {
            return null;
        }

        String sql = "SELECT id FROM menu_items WHERE name = ? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rs.getLong(1);
            }
        }
    }


    public double getTotalRevenue() {
        String sql = "SELECT SUM(total) FROM orders";
        try (Connection c = Db.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getTotalOrdersCount() {
        String sql = "SELECT COUNT(*) FROM orders";
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

    public int getOrdersCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double getRevenueToday() {
        String sql = "SELECT SUM(total) FROM orders WHERE placed_at >= CURRENT_DATE";
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

    public List<com.javaproject.model.Order> getOrdersByUser(long userId) {
        List<com.javaproject.model.Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY placed_at DESC";
        
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    orders.add(new com.javaproject.model.Order(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("placed_at"),
                        rs.getString("status"),
                        rs.getBoolean("delivery"),
                        rs.getString("promo_code"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("tax"),
                        rs.getDouble("delivery_fee"),
                        rs.getDouble("discount"),
                        rs.getDouble("total")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<CartLine> getOrderItems(long orderId) {
         List<CartLine> lines = new ArrayList<>();
         String sql = "SELECT item_name, unit_price, quantity FROM order_lines WHERE order_id = ?";
         try (Connection c = Db.getConnection();
              PreparedStatement ps = c.prepareStatement(sql)) {
             ps.setLong(1, orderId);
             try (ResultSet rs = ps.executeQuery()) {
                 while (rs.next()) {
                     // We reconstruct a lightweight CartLine with a dummy MenuItem just for display
                     com.javaproject.model.MenuItem mi = new com.javaproject.model.MenuItem(
                         rs.getString("item_name"), "", rs.getDouble("unit_price"), "", "", 0, false
                     );
                     lines.add(new CartLine(mi, rs.getInt("quantity")));
                 }
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
         return lines;
    }

    // Analytic: Weekly Revenue
    // Returns list of [Day String (e.g. "Mon"), Amount Double]
    public List<java.util.Map.Entry<String, Double>> getWeeklyRevenue() {
        List<java.util.Map.Entry<String, Double>> data = new ArrayList<>();
        // Postgres-specific: Use generate_series to ensure all last 7 days are returned
        String sql = "SELECT to_char(d.day, 'Dy') as day_label, COALESCE(SUM(o.total), 0) as day_total " +
                     "FROM generate_series(CURRENT_DATE - INTERVAL '6 days', CURRENT_DATE, '1 day'::interval) as d(day) " +
                     "LEFT JOIN orders o ON o.placed_at::DATE = d.day::DATE " +
                     "GROUP BY d.day " +
                     "ORDER BY d.day ASC";
                     
        try (Connection c = Db.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                 data.add(java.util.Map.entry(
                     rs.getString("day_label"), 
                     rs.getDouble("day_total")
                 ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Analytic: Popular Items
    // Returns list of [Item Name, Count]
    public List<java.util.Map.Entry<String, Integer>> getTopSellingItems() {
        List<java.util.Map.Entry<String, Integer>> data = new ArrayList<>();
        String sql = "SELECT item_name, SUM(quantity) as total_qty " +
                     "FROM order_lines " +
                     "GROUP BY item_name " +
                     "ORDER BY total_qty DESC " +
                     "LIMIT 5";

        try (Connection c = Db.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                 data.add(java.util.Map.entry(
                     rs.getString("item_name"), 
                     rs.getInt("total_qty")
                 ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public record OrderSummary(long id, String status, String username, String firstName, String lastName, String items, BigDecimal total) {

        public String displayCustomer() {
            String fn = firstName == null ? "" : firstName.trim();
            String ln = lastName == null ? "" : lastName.trim();
            String full = (fn + " " + ln).trim();
            if (!full.isBlank()) {
                return full;
            }

            String u = username == null ? "" : username.trim();
            return u.isBlank() ? "Guest" : u;
        }
    }
}
