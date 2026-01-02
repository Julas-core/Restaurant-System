module com.javaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens com.javaproject to javafx.fxml;
    opens com.javaproject.model to javafx.base;
    exports com.javaproject;
    exports com.javaproject.model;
}
