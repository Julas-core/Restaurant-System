package com.javaproject.db;

public final class DbConfig {
    // You can set these as environment variables:
    // - DB_URL (preferred) e.g. jdbc:postgresql://localhost:5432/resturantjava
    // - DB_USER
    // - DB_PASSWORD
    // Or the PG* variants:
    // - PGHOST, PGPORT, PGDATABASE, PGUSER, PGPASSWORD

    private final String url;
    private final String user;
    private final String password;

    private DbConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DbConfig fromEnv() {
        // We will hardcode your settings here to make sure they are picked up.
        String url = "jdbc:postgresql://localhost:5432/restaurantjava";
        String user = "postgres";
        String password = "admin123";

        // This allows environment variables to override if needed, but defaults to your values above.
        String envUrl = System.getenv("DB_URL");
        if (envUrl != null && !envUrl.isBlank()) url = envUrl;

        String envUser = System.getenv("DB_USER");
        if (envUser != null && !envUser.isBlank()) user = envUser;
        
        String envPass = System.getenv("DB_PASSWORD");
        if (envPass != null && !envPass.isBlank()) password = envPass;

        return new DbConfig(url, user, password);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
