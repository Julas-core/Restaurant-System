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
        String url = env("DB_URL");
        String user = env("DB_USER");
        String password = env("DB_PASSWORD");

        if (isBlank(url)) {
            String host = defaultIfBlank(env("PGHOST"), "localhost");
            String port = defaultIfBlank(env("PGPORT"), "5432");
            String db = defaultIfBlank(env("PGDATABASE"), "resturantjava");
            url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        }

        if (isBlank(user)) {
            user = env("PGUSER");
        }
        if (password == null) {
            password = System.getenv("PGPASSWORD");
        }

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

    private static String env(String name) {
        String v = System.getenv(name);
        return v == null ? "" : v.trim();
    }

    private static String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isBlank();
    }
}
