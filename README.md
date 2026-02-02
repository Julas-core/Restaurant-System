# Restaurant Management System

A comprehensive restaurant management application built with JavaFX and PostgreSQL, specifically designed for Ethiopian restaurant operations. This application provides both customer-facing and administrative interfaces for managing orders, menus, users, and feedback.

## Features

### Customer Features
- **User Authentication**: Secure login and registration system
- **Menu Browsing**: Browse through various Ethiopian dishes with images
- **Order Management**: Add items to cart and place orders
- **Profile Management**: Manage user profile and preferences
- **Favorites**: Save favorite dishes for quick ordering
- **Feedback**: Submit feedback about food and service

### Admin Features
- **Dashboard**: Overview of restaurant operations
- **Order Management**: View and manage customer orders
- **Menu Management**: Add, edit, and remove menu items
- **User Management**: Manage customer accounts
- **Feedback Management**: Review customer feedback
- **Settings**: Configure application settings and promotions

## Technologies Used

- **Frontend**: JavaFX 21.0.5
- **Backend**: Java 21
- **Database**: PostgreSQL 42.7.4
- **Build Tool**: Apache Maven
- **UI**: FXML for UI layouts with custom CSS styling

## Prerequisites

Before running this application, ensure you have the following installed:

- Java Development Kit (JDK) 21 or higher
- Apache Maven 3.6 or higher
- PostgreSQL database server
- JavaFX SDK 21.0.5 (included via Maven dependencies)

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Julas-core/resturantjava.git
   cd resturantjava
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Database Setup**
   - Create a PostgreSQL database
   - Configure database connection settings in the application
   - The application will automatically initialize the database schema on first run

## Running the Application

### Using Maven
```bash
mvn clean javafx:run
```

### Using PowerShell (Windows)
```powershell
.\run-javafx.ps1
```

### Manual Execution
```bash
mvn clean compile
mvn javafx:run
```

## Project Structure

```
resturantjava/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/javaproject/
│       │       ├── auth/          # Authentication logic
│       │       ├── db/            # Database access layer
│       │       ├── model/         # Data models
│       │       ├── *Controller.java  # UI controllers
│       │       └── App.java       # Main application entry point
│       └── resources/
│           └── com/javaproject/
│               ├── *.fxml         # UI layout files
│               └── styles.css     # Application styling
├── images/                        # Menu item images
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## Database Configuration

The application uses PostgreSQL for data persistence. It includes:
- Automatic schema initialization
- Data access objects (DAOs) for all entities
- Support for users, orders, menu items, favorites, feedback, and promotions

On startup, the application will:
1. Attempt to connect to the configured database
2. Initialize the schema if needed
3. Load menu items from the database
4. Fallback to in-memory menu if database is unavailable

## Development

### Building the Project
```bash
mvn clean package
```

### Running Tests
```bash
mvn test
```

## Default Login

The application starts with a login screen. Admin credentials will be set up during database initialization.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is available for educational and commercial use.

## Support

For issues, questions, or contributions, please open an issue in the GitHub repository.

## Acknowledgments

- JavaFX community for excellent UI framework
- PostgreSQL for robust database support
- Ethiopian cuisine for inspiration
