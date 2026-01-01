module com.javaproject {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.javaproject to javafx.fxml;
    exports com.javaproject;
}
