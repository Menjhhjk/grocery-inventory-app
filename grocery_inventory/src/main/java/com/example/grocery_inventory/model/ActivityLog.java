package com.example.grocery_inventory.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ActivityLog {
    private final StringProperty action = new SimpleStringProperty();
    private final StringProperty entity = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty performedBy = new SimpleStringProperty();
    private final StringProperty performedAt = new SimpleStringProperty();

    public ActivityLog() {
    }

    public ActivityLog(String action, String entity, String description, String performedBy, String performedAt) {
        setAction(action);
        setEntity(entity);
        setDescription(description);
        setPerformedBy(performedBy);
        setPerformedAt(performedAt);
    }

    public StringProperty actionProperty() {
        return action;
    }

    public StringProperty entityProperty() {
        return entity;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty performedByProperty() {
        return performedBy;
    }

    public StringProperty performedAtProperty() {
        return performedAt;
    }

    public String getAction() {
        return action.get();
    }

    public void setAction(String action) {
        this.action.set(action);
    }

    public String getEntity() {
        return entity.get();
    }

    public void setEntity(String entity) {
        this.entity.set(entity);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getPerformedBy() {
        return performedBy.get();
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy.set(performedBy);
    }

    public String getPerformedAt() {
        return performedAt.get();
    }

    public void setPerformedAt(String performedAt) {
        this.performedAt.set(performedAt);
    }
}
