package com.example.grocery_inventory.controller;

import com.example.grocery_inventory.app.InventoryApplication;
import com.example.grocery_inventory.model.User;
import com.example.grocery_inventory.repository.ActivityLogRepository;
import com.example.grocery_inventory.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserRepository userRepository = new UserRepository();
    private final ActivityLogRepository logRepository = new ActivityLogRepository();

    @FXML
    private void onLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText("Username and password are required.");
            return;
        }

        try {
            Optional<User> user = userRepository.findByUsernameAndPassword(username, password);
            if (user.isPresent()) {
                logRepository.log("LOGIN_SUCCESS", "auth", "Successful login for " + username, username);
                openInventory(user.get());
            } else {
                logRepository.log("LOGIN_FAILED", "auth", "Failed login for " + username, username);
                errorLabel.setText("Invalid username or password.");
                passwordField.clear();
            }
        } catch (SQLException ex) {
            errorLabel.setText("Login failed: " + ex.getMessage());
            passwordField.clear();
        } catch (IOException ex) {
            errorLabel.setText("Could not open inventory screen: " + ex.getMessage());
        }
    }

    private void openInventory(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(InventoryApplication.class.getResource("/com/example/grocery_inventory/inventory-view.fxml"));
        TabPane root = loader.load();
        Tab inventoryTab = root.getTabs().get(0);
        InventoryController controller = (InventoryController) inventoryTab.getProperties().get("fx:controller");
        if (controller == null) {
            controller = loader.getController();
        }
        if (controller != null) {
            controller.setCurrentUser(user.getUsername(), user.getRole());
        }

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Grocery Inventory Management");
        stage.setScene(new Scene(root, 1000, 650));
    }
}
