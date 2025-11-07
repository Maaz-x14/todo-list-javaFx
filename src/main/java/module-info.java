module com.example.todoappjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens com.example.todoappjavafx to javafx.fxml;
    exports com.example.todoappjavafx;
}