package com.example.grocery_inventory.controller;

import com.example.grocery_inventory.model.ActivityLog;
import com.example.grocery_inventory.repository.ActivityLogRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LogController implements Initializable {
    @FXML private TableView<ActivityLog> logTable;
    @FXML private TableColumn<ActivityLog, String> colLogAt;
    @FXML private TableColumn<ActivityLog, String> colLogBy;
    @FXML private TableColumn<ActivityLog, String> colLogAction;
    @FXML private TableColumn<ActivityLog, String> colLogEntity;
    @FXML private TableColumn<ActivityLog, String> colLogDesc;

    private final ActivityLogRepository logRepository = new ActivityLogRepository();
    private final ObservableList<ActivityLog> logList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colLogAt.setCellValueFactory(new PropertyValueFactory<>("performedAt"));
        colLogBy.setCellValueFactory(new PropertyValueFactory<>("performedBy"));
        colLogAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colLogEntity.setCellValueFactory(new PropertyValueFactory<>("entity"));
        colLogDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        logTable.setItems(logList);
        loadAll();
    }

    @FXML
    private void onTabSelected(Event event) {
        loadAll();
    }

    @FXML
    private void onFilterAll() {
        loadAll();
    }

    @FXML
    private void onFilterProducts() {
        loadByEntity("product");
    }

    @FXML
    private void onFilterLogins() {
        loadByEntity("auth");
    }

    @FXML
    private void onRefresh() {
        loadAll();
    }

    private void loadAll() {
        try {
            logList.setAll(logRepository.findAll());
        } catch (SQLException ex) {
            System.err.println("Failed to load activity logs: " + ex.getMessage());
        }
    }

    private void loadByEntity(String entity) {
        try {
            logList.setAll(logRepository.findByEntity(entity));
        } catch (SQLException ex) {
            System.err.println("Failed to load activity logs: " + ex.getMessage());
        }
    }
}
