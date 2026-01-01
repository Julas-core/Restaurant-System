module com.javaproject {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.javaproject to javafx.fxml;
    opens com.javaproject.model to javafx.base;
    exports com.javaproject;
    exports com.javaproject.model;
}
