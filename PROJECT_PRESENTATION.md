# Restaurant Management System - Project Presentation

## 1. Project Overview
**Title:** Java Restaurant Management System  
**Description:** A desktop application built with **JavaFX** and **PostgreSQL** to manage restaurant operations, including online ordering, menu management, and admin dashboards.

This document contains technical details suitable for developer documentation and slide content for presentations.

---

## 2. Main Layouts (FXML Views)
The application user interface is built using JavaFX FXML files. The majority of pages utilize a **BorderPane** layout for a standard Top/Center/Left/Right organisation, while specific sub-views or forms use simpler layouts like **VBox**.

### Client / User Facing
*   **Authentication:**
    *   `login.fxml` (**BorderPane**) - User login screen with hero image and form.
    *   `register.fxml` (**BorderPane**) - New user registration with split view.
*   **Main Navigation:**
    *   `home.fxml` (**BorderPane**) - Main dashboard with top navigation.
    *   `about.fxml` (**BorderPane**) - Information page.
    *   `contact.fxml` (**BorderPane**) - Contact form page.
    *   `location.fxml` (**BorderPane**) - Location information view.
*   **Ordering & Dining:**
    *   `meal.fxml` (**BorderPane**) - Menu listing with top bar and grid/list content.
    *   `checkout.fxml` (**BorderPane**) - Checkout screens.
*   **User Account:**
    *   `profile.fxml` (**BorderPane**) - User profile settings and history.
    *   `feedback.fxml` (**BorderPane**) - Feedback form.

### Administration Dashboard
*   **Core Dashboard:**
    *   `admin_dashboard.fxml` (**BorderPane**) - Admin layout with Sidebar (Left) and Content (Center).
*   **Management Views:**
    *   `admin_meals.fxml` (**BorderPane**) - Table view for managing meals.
    *   `admin_meal_add.fxml` (**VBox**) - Form for adding new meals (often used as a dialog/popup).
    *   `admin_orders.fxml` (**BorderPane**) - Orders management table.
    *   `admin_users.fxml` (**BorderPane**) - User management table.
    *   `admin_feedback.fxml` (**BorderPane**) - Feedback review list.
    *   `admin_settings.fxml` (**BorderPane**) - Application configuration settings.

### Common/Utility
*   `primary.fxml` (**BorderPane**) - Template for main application windows.
*   `secondary.fxml` (**VBox**) - Template for secondary windows or dialogs.
*   `styles.css` - Global styling for the application.

---

## 3. Database & JDBC Configuration
The application uses raw **JDBC (Java Database Connectivity)** for direct, efficient communication with a **PostgreSQL** database.

### Connection Details (`DbConfig.java`)
*   **Driver:** PostgreSQL JDBC Driver.
*   **Default Connection URL:** `jdbc:postgresql://localhost:5432/restaurantjava`
*   **Credentials:** Configured via `DbConfig` (defaults: `postgres` / `admin123`) or Environment Variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`).

### Data Access Objects (DAOs)
The data layer is organized using the DAO pattern in the `com.javaproject.db` package:

| Class Name | Functionality |
| :--- | :--- |
| **`UserDao`** | Manages user authentication, registration, and role retrieval. |
| **`MenuItemDao`** | Handles menu items (fetching list, adding meals, updating prices). |
| **`OrderDao`** | Processes customer orders and tracks order status. |
| **`FeedbackDao`** | Stores and retrieves customer feedback. |
| **`ProfileDao`** | Manages user profile updates. |
| **`PromoDao`** | Handles promotional codes and discounts. |
| **`ConfigDao`** | Manages dynamic application settings stored in the DB. |

### Utilities
*   **`Db.java`**: Singleton/Static wrapper for obtaining database connections using `DriverManager`.
*   **`PasswordHasher`**: Ensures security by hashing user passwords before storage.
*   **`SchemaInitializer` / `DbBootstrap`**: Handles database setup and seeding initial data.

---

## 4. Presentation Slides Content
*Use the following sections as content for your slide deck.*

### Slide 1: Title Slide
# Restaurant Management System
### A comprehensive Desktop Solution
**Built with:** Java, JavaFX, PostgreSQL

---

### Slide 2: Problem & Solution
**The Challenge:**
*   Restaurants need an efficient way to manage digital orders.
*   Admins need a centralized hub to track sales, users, and menu items.

**The Solution:**
*   A robust desktop application providing tailored interfaces for **Customers** (ordering) and **Administrators** (management).
*   Reliable data persistence using an industry-standard SQL database.

---

### Slide 3: Technology Stack
*   **Frontend:** JavaFX (FXML)
    *   Rich desktop UI experience.
    *   CSS styling for modern look and feel.
*   **Backend Logic:** Java 25 (Modular)
    *   MVC (Model-View-Controller) Architecture.
*   **Database:** PostgreSQL
    *   Direct JDBC implementation for performance.
    *   Secure password handling.

---

### Slide 4: Key Features
**For Customers:**
*   Browse Menu Items.
*   Secure Login/Registration.
*   Cart & Checkout system.
*   Profile Management.

**For Administrators:**
*   **Real-time Dashboard:** Sales and user metrics.
*   **Content Management:** Add/Edit/Delete Meals.
*   **Order Tracking:** Monitor order status.

---

### Slide 5: Future Improvements
*   Migration to JPA/Hibernate for easier data handling.
*   Integration with Payment Gateways.
*   Mobile App interface.
