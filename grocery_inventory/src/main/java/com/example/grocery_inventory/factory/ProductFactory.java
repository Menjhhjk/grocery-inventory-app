package com.example.grocery_inventory.factory;

import com.example.grocery_inventory.model.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ProductFactory {
    private ProductFactory() {
    }

    public static Product fromResultSet(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("barcode"),
                rs.getString("name"),
                rs.getInt("category_id"),
                rs.getString("category_name"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getString("unit")
        );
    }
}
