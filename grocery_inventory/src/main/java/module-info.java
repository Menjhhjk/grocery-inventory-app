module com.example.grocery_inventory {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires bcrypt;

    uses java.sql.Driver;

    opens com.example.grocery_inventory.controller to javafx.fxml;
    opens com.example.grocery_inventory.model to javafx.base;
    exports com.example.grocery_inventory.app;
}
