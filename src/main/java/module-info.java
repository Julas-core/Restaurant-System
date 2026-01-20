module com.javaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires org.postgresql.jdbc;
    requires javafx.graphics;

    opens com.javaproject to javafx.fxml;
    opens com.javaproject.model to javafx.base;
    exports com.javaproject;
    exports com.javaproject.model;
    exports com.javaproject.auth;
}
