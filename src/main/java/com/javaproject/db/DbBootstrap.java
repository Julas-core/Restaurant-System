package com.javaproject.db;

import com.javaproject.model.MenuItem;
import com.javaproject.auth.Role;

import java.sql.SQLException;
import java.util.List;

public final class DbBootstrap {

    private DbBootstrap() {
    }

    public static List<MenuItem> initAndLoadMenu(List<MenuItem> fallbackMenu) throws SQLException {
        SchemaInitializer.init();

        // Seed demo users (hashed passwords)
        UserDao userDao = new UserDao();
        ProfileDao profileDao = new ProfileDao();
        userDao.ensureSeedUser("admin", "admin123", Role.ADMIN);
        userDao.ensureSeedUser("user", "user123", Role.USER);
        profileDao.ensureDefaultProfile("admin");
        profileDao.ensureDefaultProfile("user");

        // Seed menu
        MenuItemDao menuDao = new MenuItemDao();
        if (menuDao.isEmpty() && fallbackMenu != null && !fallbackMenu.isEmpty()) {
            menuDao.seed(fallbackMenu);
        }

        return menuDao.listActive();
    }
}
