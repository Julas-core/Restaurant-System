package com.javaproject.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaInitializer {

    private SchemaInitializer() {
    }

    public static void init() throws SQLException {
        try (Connection c = Db.getConnection(); Statement s = c.createStatement()) {
            // Enable referential integrity checks (Postgres enforces by default, but keep for parity).
            s.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password_salt BYTEA NOT NULL," +
                    "password_hash BYTEA NOT NULL," +
                    "password_iterations INT NOT NULL," +
                    "role TEXT NOT NULL," +
                    "created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()" +
                    ")");

            s.execute("CREATE TABLE IF NOT EXISTS profiles (" +
                    "user_id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE," +
                    "first_name TEXT NOT NULL DEFAULT ''," +
                    "last_name TEXT NOT NULL DEFAULT ''," +
                    "email TEXT NOT NULL DEFAULT ''," +
                    "phone TEXT NOT NULL DEFAULT ''," +
                    "addr_home TEXT NOT NULL DEFAULT ''," +
                    "addr_work TEXT NOT NULL DEFAULT ''" +
                    ")");

            s.execute("CREATE TABLE IF NOT EXISTS menu_items (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "name TEXT NOT NULL UNIQUE," +
                    "description TEXT NOT NULL DEFAULT ''," +
                    "price NUMERIC(10,2) NOT NULL," +
                    "category TEXT NOT NULL DEFAULT ''," +
                    "label TEXT NOT NULL DEFAULT ''," +
                    "rating NUMERIC(3,2) NOT NULL DEFAULT 0," +
                    "chef_special BOOLEAN NOT NULL DEFAULT FALSE," +
                    "active BOOLEAN NOT NULL DEFAULT TRUE" +
                    ")");

            s.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "user_id BIGINT NULL REFERENCES users(id) ON DELETE SET NULL," +
                    "placed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()," +
                    "status TEXT NOT NULL DEFAULT 'PLACED'," +
                    "delivery BOOLEAN NOT NULL DEFAULT TRUE," +
                    "promo_code TEXT NULL," +
                    "subtotal NUMERIC(10,2) NOT NULL DEFAULT 0," +
                    "tax NUMERIC(10,2) NOT NULL DEFAULT 0," +
                    "delivery_fee NUMERIC(10,2) NOT NULL DEFAULT 0," +
                    "discount NUMERIC(10,2) NOT NULL DEFAULT 0," +
                    "total NUMERIC(10,2) NOT NULL DEFAULT 0" +
                    ")");

            s.execute("CREATE TABLE IF NOT EXISTS order_lines (" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE," +
                    "menu_item_id BIGINT NULL REFERENCES menu_items(id) ON DELETE SET NULL," +
                    "item_name TEXT NOT NULL," +
                    "unit_price NUMERIC(10,2) NOT NULL," +
                    "quantity INT NOT NULL," +
                    "notes TEXT NOT NULL DEFAULT ''" +
                    ")");
        }
    }
}
