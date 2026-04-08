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
- [Comprehensive Repository Review](#comprehensive-repository-review)
- [Known Issues](#known-issues)
- [Troubleshooting](#troubleshooting)

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
This project seeds demo accounts when the database is empty.

For safety, explicit credentials are intentionally not documented here.  
Inspect the seed/auth code paths if you need to change or remove demo-user behavior for your environment.

> Recommended for production: remove demo users entirely and require environment-specific bootstrap logic.

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

## Comprehensive Repository Review

### What’s strong
- Clear layering between UI controllers, auth, models, and DAOs.
- Parameterized SQL is used broadly in DAOs, reducing SQL injection risk.
- Password hashing exists (PBKDF2 with per-user salt + iteration count).
- Startup bootstrap strategy allows fallback menu behavior when DB is unavailable.
- Admin pages cover core operational use cases (orders, users, meals, feedback, settings).

### Risks and gaps
1. **Security: hardcoded DB credentials and demo users**
   - `DbConfig` includes embedded defaults for URL/user/password.
   - Seeded demo credentials are static and predictable.
2. **Critical Security: role escalation path (immediate fix required)**
   - Registration flow allows selecting admin role from UI.
3. **Operational maturity**
   - No automated test suite (`src/test` is absent).
   - No CI workflow files present.
4. **Build portability**
   - Requires Java 21; environments with older JDKs fail compilation.
5. **Code quality/maintainability**
   - Some controllers are large and combine UI + business logic.
   - Exception handling is often `printStackTrace()` with limited structured logging.
6. **Documentation baseline**
   - Repository previously lacked a README, license file, and contribution guide.

### Recommended priorities
1. Remove hardcoded credentials and require environment-based secrets.
2. Restrict admin role assignment (server-side policy, not UI checkbox).
3. Add automated tests (auth/DAO/core state), then add CI.
4. Refactor large controllers into service/helper layers.
5. Add standardized logging and error reporting.
6. Add `LICENSE` and `CONTRIBUTING.md`.

## Known Issues
- Local validation in this environment failed with:
  - `error: release version 21 not supported`
- This indicates the current environment JDK is below 21. Use JDK 21 to build/run.

## Troubleshooting
- **Cannot connect to DB**: verify `DB_URL`, `DB_USER`, `DB_PASSWORD`, and PostgreSQL availability.
- **JavaFX run issues**: confirm JDK 21 and run `mvn -v` to check active Java version.
- **Blank/partial data**: schema and seed run at startup; check DB permissions and startup logs.
