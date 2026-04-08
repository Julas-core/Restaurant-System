# Restaurant System

A JavaFX + PostgreSQL restaurant management application with customer ordering flows and an admin panel for operations.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Default Seed Accounts](#default-seed-accounts)
- [Project Structure](#project-structure)
  
## Overview
This project implements an end-to-end restaurant experience:
- Customers can browse menu items, filter/search, add to cart, apply promo codes, and place orders.
- Authenticated users can manage profile data and submit feedback.
- Admin users can manage meals, users, orders, feedback, and selected system settings.

The application initializes database schema and seeds initial data at startup when PostgreSQL is available.

## Features
- User registration and login (role-based: `USER`, `ADMIN`)
- Menu browsing with category tabs, filters, and search
- Cart and checkout flow with:
  - Delivery/pickup toggle
  - Promo code support
  - Tax and delivery fee calculation
- Order persistence and order line storage
- Feedback submission and admin feedback management
- Admin dashboard metrics (revenue, orders, ratings, popular items)
- Admin settings for tax rate and delivery fee

## Tech Stack
- Java 21
- JavaFX 21
- Maven
- PostgreSQL (JDBC driver)

## Architecture
- **UI Layer**: JavaFX FXML views + controllers in `com.javaproject`
- **Domain Models**: `com.javaproject.model`
- **Auth Layer**: `com.javaproject.auth`
- **Persistence Layer**: DAOs in `com.javaproject.db`
- **Application State**: `AppState` singleton-like state used by controllers

### Data Flow (high-level)
1. App starts and loads `login.fxml`.
2. DB bootstrap attempts schema initialization + data seeding.
3. Controllers read/write `AppState` and use DAO classes for persistence.
4. Navigation is scene-root based (`App.setRoot(...)`).

## Prerequisites
- JDK 21
- Maven 3.9+ (or compatible)
- PostgreSQL running locally or remotely

## Getting Started
```bash
# 1) Clone and enter repository
git clone <repo-url>
cd Restaurant-System

# 2) Configure DB (see Configuration section)

# 3) Run app
mvn clean javafx:run
```

On Windows, you can also use:
```powershell
.\run-javafx.ps1
```

## Configuration
Database config is read from environment variables (with internal defaults in code):

- `DB_URL` (example: `jdbc:postgresql://localhost:5432/restaurantjava`)
- `DB_USER`
- `DB_PASSWORD`

If not set, the app currently defaults to local values in `DbConfig`.

## Default Seed Accounts
When schema seeding runs and users do not already exist:
- Admin: `admin / admin123`
- User: `user / user123`

> Recommended for production: rotate/remove demo credentials and disable permissive role creation paths.

## Project Structure
```text
Restaurant-System/
├── pom.xml
├── run-javafx.ps1
├── images/
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java
        │   └── com/javaproject/
        │       ├── controllers (App, PrimaryController, CheckoutController, admin controllers, etc.)
        │       ├── auth/ (AuthService, Role, User)
        │       ├── db/ (Db, SchemaInitializer, DAO classes)
        │       └── model/ (MenuItem, Order, CartLine, Feedback)
        └── resources/com/javaproject/
            ├── *.fxml
            ├── styles.css
            └── images/
```
