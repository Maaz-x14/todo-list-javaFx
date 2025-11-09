module com.example.todoappjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    // These 'opens' directives allow FXML/Gson to use reflection
    opens com.example.todoappjavafx to javafx.fxml;
    opens com.example.todoappjavafx.controller to javafx.fxml;
    opens com.example.todoappjavafx.model to com.google.gson;

    // 🛑 THIS IS THE FIX YOU WERE MISSING
    opens com.example.todoappjavafx.view to javafx.fxml;


    exports com.example.todoappjavafx;
    exports com.example.todoappjavafx.controller;
}