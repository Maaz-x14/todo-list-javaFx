module com.example.todoappjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.todoappjavafx to javafx.fxml;
    opens com.example.todoappjavafx.controller to javafx.fxml;
    opens com.example.todoappjavafx.model to com.google.gson;

    exports com.example.todoappjavafx;
    exports com.example.todoappjavafx.controller;
}
