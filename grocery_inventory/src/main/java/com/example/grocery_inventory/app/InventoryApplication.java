package com.example.grocery_inventory.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class InventoryApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(InventoryApplication.class.getResource("/com/example/grocery_inventory/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        stage.setTitle("Grocery Inventory - Login");
        stage.setScene(scene);
        stage.show();
    }
}
