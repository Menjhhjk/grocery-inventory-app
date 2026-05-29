package com.example.grocery_inventory.controller;

import com.example.grocery_inventory.model.ActivityLog;
import com.example.grocery_inventory.model.Product;
import com.example.grocery_inventory.model.User;
import com.example.grocery_inventory.app.InventoryApplication;
import com.example.grocery_inventory.repository.ActivityLogRepository;
import com.example.grocery_inventory.repository.ProductRepository;
import com.example.grocery_inventory.repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {
    @FXML private TabPane mainTabPane;
    @FXML private Tab inventoryTab;
    @FXML private Tab logTab;
    @FXML private Tab accountTab;
    @FXML private TextField searchField;
    @FXML private Button newProductButton;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> colBarcode;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, String> colUnit;
    @FXML private TextField barcodeField;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextField unitField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private VBox inventoryFormBox;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    @FXML private TableView<ActivityLog> logTable;
    @FXML private TableColumn<ActivityLog, String> colLogAt;
    @FXML private TableColumn<ActivityLog, String> colLogBy;
    @FXML private TableColumn<ActivityLog, String> colLogAction;
    @FXML private TableColumn<ActivityLog, String> colLogEntity;
    @FXML private TableColumn<ActivityLog, String> colLogDesc;

    @FXML private TextField accountSearchField;
    @FXML private TableView<User> accountTable;
    @FXML private TableColumn<User, String> colAccountUsername;
    @FXML private TableColumn<User, String> colAccountRole;
    @FXML private TextField accountUsernameField;
    @FXML private PasswordField accountPasswordField;
    @FXML private PasswordField accountConfirmPasswordField;
    @FXML private ComboBox<String> accountRoleCombo;
    @FXML private Label accountStatusLabel;

    private final ProductRepository repository = new ProductRepository();
    private final ActivityLogRepository logRepository = new ActivityLogRepository();
    private final UserRepository userRepository = new UserRepository();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final ObservableList<ActivityLog> logList = FXCollections.observableArrayList();
    private final ObservableList<User> accountList = FXCollections.observableArrayList();
    private Product selectedProduct = null;
    private User selectedAccount = null;
    private String currentUser = "unknown";
    private String currentRole = "VIEWER";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (inventoryTab != null) {
            inventoryTab.getProperties().put("fx:controller", this);
        }

        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        categoryCombo.getItems().setAll(
                "Beverages", "Dairy", "Bakery", "Meat & Seafood",
                "Fruits & Vegetables", "Snacks", "Canned Goods", "Condiments"
        );
        productTable.setItems(productList);
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                populateForm(newValue);
            }
        });

        bindLogColumns();
        logTable.setItems(logList);

        colAccountUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colAccountRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        accountTable.setItems(accountList);
        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                populateAccountForm(newValue);
            }
        });
        accountRoleCombo.getItems().setAll("OPERATOR", "VIEWER");
        accountRoleCombo.getSelectionModel().select("OPERATOR");

        loadAll();
        loadAllLogs();
        loadAccounts();
    }

    @FXML
    private void onSave() {
        if (!canManageInventory()) {
            setStatus("Your account can only view inventory.", true);
            return;
        }
        if (!validateForm()) {
            return;
        }

        Product product = buildProductFromForm();
        try {
            repository.insert(product);
            logRepository.log("CREATE", "product", "Created product: " + product.getName(), getCurrentUser());
            loadAll();
            clearForm();
            setStatus("Product saved.", false);
        } catch (SQLException ex) {
            setStatus("Save failed: " + ex.getMessage(), true);
        }
    }

    @FXML
    private void onUpdate() {
        if (!canManageInventory()) {
            setStatus("Your account can only view inventory.", true);
            return;
        }
        if (selectedProduct == null) {
            setStatus("Select a product to update.", true);
            return;
        }
        if (!validateForm()) {
            return;
        }

        Product product = buildProductFromForm();
        product.setId(selectedProduct.getId());
        try {
            repository.update(product);
            logRepository.log("UPDATE", "product", "Updated product: " + product.getName(), getCurrentUser());
            loadAll();
            clearForm();
            setStatus("Product updated.", false);
        } catch (SQLException ex) {
            setStatus("Update failed: " + ex.getMessage(), true);
        }
    }

    @FXML
    private void onDelete() {
        if (!canManageInventory()) {
            setStatus("Your account can only view inventory.", true);
            return;
        }
        if (selectedProduct == null) {
            setStatus("Select a product to delete.", true);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Delete selected product?");
        alert.setContentText(selectedProduct.getName());

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        String productName = selectedProduct.getName();
        try {
            repository.delete(selectedProduct.getId());
            logRepository.log("DELETE", "product", "Deleted product: " + productName, getCurrentUser());
            loadAll();
            clearForm();
            setStatus("Product deleted.", false);
        } catch (SQLException ex) {
            setStatus("Delete failed: " + ex.getMessage(), true);
        }
    }

    @FXML
    private void onSearch() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();
        try {
            if (keyword.isBlank()) {
                productList.setAll(repository.findAll());
            } else {
                productList.setAll(repository.search(keyword));
            }
            setStatus(productList.size() + " product(s) loaded.", false);
        } catch (SQLException ex) {
            setStatus("Search failed: " + ex.getMessage(), true);
        }
    }

    @FXML
    private void onClear() {
        searchField.clear();
        loadAll();
    }

    @FXML
    private void onCancel() {
        clearForm();
    }

    @FXML
    private void onNewProduct() {
        if (!canManageInventory()) {
            setStatus("Your account can only view inventory.", true);
            return;
        }
        clearForm();
        barcodeField.requestFocus();
    }

    @FXML
    private void onLogout() {
        logRepository.log("LOGOUT", "auth", "Logged out " + getCurrentUser(), getCurrentUser());

        try {
            FXMLLoader loader = new FXMLLoader(InventoryApplication.class.getResource("/com/example/grocery_inventory/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            stage.setTitle("Grocery Inventory - Login");
            stage.setScene(scene);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Log Out Failed");
            alert.setHeaderText("Could not return to the login screen.");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void setCurrentUser(String username) {
        setCurrentUser(username, "VIEWER");
    }

    public void setCurrentUser(String username, String role) {
        if (username != null && !username.isBlank()) {
            currentUser = username;
        }
        if (role != null && !role.isBlank()) {
            currentRole = role.toUpperCase();
        }
        applyPermissions();
    }

    private void loadAll() {
        try {
            productList.setAll(repository.findAll());
            setStatus(productList.size() + " product(s) loaded.", false);
        } catch (SQLException ex) {
            setStatus("Load failed: " + ex.getMessage(), true);
        }
    }

    private void populateForm(Product product) {
        selectedProduct = product;
        barcodeField.setText(product.getBarcode());
        nameField.setText(product.getName());
        categoryCombo.getSelectionModel().select(product.getCategoryName());
        priceField.setText(String.format("%.2f", product.getPrice()));
        stockField.setText(String.valueOf(product.getStock()));
        unitField.setText(product.getUnit());
    }

    private Product buildProductFromForm() {
        int selectedIndex = categoryCombo.getSelectionModel().getSelectedIndex();
        int categoryId = selectedIndex + 1;
        String categoryName = categoryCombo.getSelectionModel().getSelectedItem();

        return new Product(
                0,
                barcodeField.getText().trim(),
                nameField.getText().trim(),
                categoryId,
                categoryName,
                Double.parseDouble(priceField.getText().trim()),
                Integer.parseInt(stockField.getText().trim()),
                unitField.getText().trim()
        );
    }

    private boolean validateForm() {
        if (barcodeField.getText().isBlank() || nameField.getText().isBlank()
                || priceField.getText().isBlank() || stockField.getText().isBlank()
                || unitField.getText().isBlank() || categoryCombo.getSelectionModel().isEmpty()) {
            setStatus("All product fields are required.", true);
            return false;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                setStatus("Price cannot be negative.", true);
                return false;
            }
        } catch (NumberFormatException ex) {
            setStatus("Price must be a valid number.", true);
            return false;
        }

        try {
            int stock = Integer.parseInt(stockField.getText().trim());
            if (stock < 0) {
                setStatus("Stock cannot be negative.", true);
                return false;
            }
        } catch (NumberFormatException ex) {
            setStatus("Stock must be a valid whole number.", true);
            return false;
        }

        return true;
    }

    private void clearForm() {
        barcodeField.clear();
        nameField.clear();
        priceField.clear();
        stockField.clear();
        unitField.clear();
        categoryCombo.getSelectionModel().clearSelection();
        selectedProduct = null;
        productTable.getSelectionModel().clearSelection();
        setStatus("", false);
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }

    private String getCurrentUser() {
        return currentUser;
    }

    private boolean isAdmin() {
        return "ADMIN".equals(currentRole);
    }

    private boolean canManageInventory() {
        return isAdmin() || "OPERATOR".equals(currentRole);
    }

    private boolean canSeeLogs() {
        return isAdmin() || "OPERATOR".equals(currentRole);
    }

    private void applyPermissions() {
        boolean canManage = canManageInventory();
        newProductButton.setVisible(canManage);
        newProductButton.setManaged(canManage);
        inventoryFormBox.setVisible(canManage);
        inventoryFormBox.setManaged(canManage);
        saveButton.setDisable(!canManage);
        updateButton.setDisable(!canManage);
        deleteButton.setDisable(!canManage);

        setTabVisible(logTab, canSeeLogs());
        setTabVisible(accountTab, isAdmin());
        if (isAdmin()) {
            loadAccounts();
        }
    }

    private void setTabVisible(Tab tab, boolean visible) {
        if (visible) {
            if (!mainTabPane.getTabs().contains(tab)) {
                mainTabPane.getTabs().add(tab);
            }
        } else {
            mainTabPane.getTabs().remove(tab);
        }
    }

    private void bindLogColumns() {
        colLogAt.setCellValueFactory(new PropertyValueFactory<>("performedAt"));
        colLogBy.setCellValueFactory(new PropertyValueFactory<>("performedBy"));
        colLogAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colLogEntity.setCellValueFactory(new PropertyValueFactory<>("entity"));
        colLogDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    @FXML
    private void onTabSelected(Event event) {
        if (event == null || ((Tab) event.getSource()).isSelected()) {
            loadAllLogs();
        }
    }

    @FXML
    private void onFilterAll() {
        loadAllLogs();
    }

    @FXML
    private void onFilterProducts() {
        loadLogsByEntity("product");
    }

    @FXML
    private void onFilterLogins() {
        loadLogsByEntity("auth");
    }

    @FXML
    private void onRefresh() {
        loadAllLogs();
    }

    private void loadAllLogs() {
        try {
            logList.setAll(logRepository.findAll());
        } catch (SQLException ex) {
            System.err.println("Failed to load activity logs: " + ex.getMessage());
        }
    }

    private void loadLogsByEntity(String entity) {
        try {
            logList.setAll(logRepository.findByEntity(entity));
        } catch (SQLException ex) {
            System.err.println("Failed to load activity logs: " + ex.getMessage());
        }
    }

    @FXML
    private void onSearchAccounts() {
        String keyword = accountSearchField.getText() == null ? "" : accountSearchField.getText().trim();
        try {
            if (keyword.isBlank()) {
                accountList.setAll(userRepository.findAll());
            } else {
                accountList.setAll(userRepository.search(keyword));
            }
            setAccountStatus(accountList.size() + " account(s) loaded.", false);
        } catch (SQLException ex) {
            setAccountStatus("Account search failed: " + ex.getMessage(), true);
        }
    }

    @FXML
    private void onClearAccountSearch() {
        accountSearchField.clear();
        loadAccounts();
    }

    @FXML
    private void onNewAccount() {
        clearAccountForm();
        accountUsernameField.requestFocus();
    }

    @FXML
    private void onShowRoleHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Role Permissions");
        alert.setHeaderText("Account roles");
        alert.setContentText("""
                OPERATOR
                Can manage product inventory and view activity logs.

                VIEWER
                Can view and search product inventory only.
                """);
        alert.showAndWait();
    }

    @FXML
    private void onCreateAccount() {
        if (!isAdmin()) {
            setAccountStatus("Only admins can create accounts.", true);
            return;
        }
        if (!validateAccountForm(true)) {
            return;
        }

        String username = accountUsernameField.getText() == null ? "" : accountUsernameField.getText().trim();
        String password = accountPasswordField.getText() == null ? "" : accountPasswordField.getText();
        String role = accountRoleCombo.getSelectionModel().getSelectedItem();

        try {
            userRepository.createUser(username, password, role);
            logRepository.log("CREATE_ACCOUNT", "auth", "Created " + role + " account: " + username, getCurrentUser());
            loadAccounts();
            clearAccountForm();
            setAccountStatus("Account created.", false);
            loadAllLogs();
        } catch (SQLException ex) {
            setAccountStatus("Create account failed: " + ex.getMessage(), true);
        }
    }

    @FXML
    private void onUpdateAccount() {
        if (!isAdmin()) {
            setAccountStatus("Only admins can update accounts.", true);
            return;
        }
        if (selectedAccount == null) {
            setAccountStatus("Select an account to update.", true);
            return;
        }
        if (!validateAccountForm(false)) {
            return;
        }

        String username = accountUsernameField.getText().trim();
        String password = accountPasswordField.getText() == null ? "" : accountPasswordField.getText();
        String role = accountRoleCombo.getSelectionModel().getSelectedItem();

        if (selectedAccount.getUsername().equals(currentUser) && !"ADMIN".equals(role)) {
            setAccountStatus("You cannot remove admin access from your current account.", true);
            return;
        }

        User updatedAccount = new User(selectedAccount.getId(), username, selectedAccount.getPasswordHash(), role);
        try {
            userRepository.updateUser(updatedAccount, password);
            logRepository.log("UPDATE_ACCOUNT", "auth", "Updated " + role + " account: " + username, getCurrentUser());
            loadAccounts();
            clearAccountForm();
            setAccountStatus("Account updated.", false);
            loadAllLogs();
        } catch (SQLException ex) {
            setAccountStatus("Update account failed: " + ex.getMessage(), true);
        }
    }

    @FXML
    private void onClearAccountForm() {
        clearAccountForm();
    }

    private void clearAccountForm() {
        accountUsernameField.clear();
        accountPasswordField.clear();
        accountConfirmPasswordField.clear();
        accountRoleCombo.getSelectionModel().select("OPERATOR");
        selectedAccount = null;
        accountTable.getSelectionModel().clearSelection();
    }

    private void setAccountStatus(String msg, boolean isError) {
        accountStatusLabel.setText(msg);
        accountStatusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }

    private void loadAccounts() {
        if (!isAdmin()) {
            return;
        }

        try {
            accountList.setAll(userRepository.findAll());
            setAccountStatus(accountList.size() + " account(s) loaded.", false);
        } catch (SQLException ex) {
            setAccountStatus("Load accounts failed: " + ex.getMessage(), true);
        }
    }

    private void populateAccountForm(User account) {
        selectedAccount = account;
        accountUsernameField.setText(account.getUsername());
        accountPasswordField.clear();
        accountConfirmPasswordField.clear();
        accountRoleCombo.getSelectionModel().select(account.getRole());
        setAccountStatus("Editing account. Leave password blank to keep current password.", false);
    }

    private boolean validateAccountForm(boolean passwordRequired) {
        String username = accountUsernameField.getText() == null ? "" : accountUsernameField.getText().trim();
        String password = accountPasswordField.getText() == null ? "" : accountPasswordField.getText();
        String confirmPassword = accountConfirmPasswordField.getText() == null ? "" : accountConfirmPasswordField.getText();
        String role = accountRoleCombo.getSelectionModel().getSelectedItem();

        if (username.isBlank() || role == null) {
            setAccountStatus("Username and role are required.", true);
            return false;
        }
        if ((passwordRequired || !password.isBlank() || !confirmPassword.isBlank()) && password.length() < 8) {
            setAccountStatus("Password must be at least 8 characters.", true);
            return false;
        }
        if (!password.equals(confirmPassword)) {
            setAccountStatus("Passwords do not match.", true);
            return false;
        }

        return true;
    }
}
