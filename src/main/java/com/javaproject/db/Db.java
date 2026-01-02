package com.javaproject.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {

    private static volatile DbConfig config;

    private Db() {
    }

    public static void configure(DbConfig cfg) {
        config = cfg;
    }

    public static Connection getConnection() throws SQLException {
        DbConfig cfg = config;
        if (cfg == null) {
            cfg = DbConfig.fromEnv();
            config = cfg;
        }

        if (cfg.getUser() == null || cfg.getUser().isBlank()) {
            // Allow local trust auth setups that don't need explicit user.
            return DriverManager.getConnection(cfg.getUrl());
        }

        if (cfg.getPassword() == null || cfg.getPassword().isBlank()) {
            return DriverManager.getConnection(cfg.getUrl(), cfg.getUser(), "");
        }

        return DriverManager.getConnection(cfg.getUrl(), cfg.getUser(), cfg.getPassword());
    }
}
