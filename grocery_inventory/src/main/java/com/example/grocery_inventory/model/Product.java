package com.example.grocery_inventory.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty barcode = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty categoryId = new SimpleIntegerProperty();
    private final StringProperty categoryName = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final IntegerProperty stock = new SimpleIntegerProperty();
    private final StringProperty unit = new SimpleStringProperty();

    public Product() {
    }

    public Product(int id, String barcode, String name, int categoryId, String categoryName, double price, int stock, String unit) {
        setId(id);
        setBarcode(barcode);
        setName(name);
        setCategoryId(categoryId);
        setCategoryName(categoryName);
        setPrice(price);
        setStock(stock);
        setUnit(unit);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty barcodeProperty() {
        return barcode;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty categoryIdProperty() {
        return categoryId;
    }

    public StringProperty categoryNameProperty() {
        return categoryName;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public StringProperty unitProperty() {
        return unit;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getBarcode() {
        return barcode.get();
    }

    public void setBarcode(String barcode) {
        this.barcode.set(barcode);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getCategoryId() {
        return categoryId.get();
    }

    public void setCategoryId(int categoryId) {
        this.categoryId.set(categoryId);
    }

    public String getCategoryName() {
        return categoryName.get();
    }

    public void setCategoryName(String categoryName) {
        this.categoryName.set(categoryName);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public String getUnit() {
        return unit.get();
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    @Override
    public String toString() {
        return getName();
    }
}
