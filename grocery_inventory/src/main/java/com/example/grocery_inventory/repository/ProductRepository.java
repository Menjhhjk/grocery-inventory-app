package com.example.grocery_inventory.repository;

import com.example.grocery_inventory.factory.ProductFactory;
import com.example.grocery_inventory.model.Product;
import com.example.grocery_inventory.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static final String BASE_SELECT = """
            SELECT p.id, p.barcode, p.name, p.category_id, c.name AS category_name, p.price, p.stock, p.unit
            FROM products p
            JOIN categories c ON c.id = p.category_id
            """;

    public List<Product> findAll() throws SQLException {
        String sql = BASE_SELECT + " ORDER BY p.name";
        List<Product> products = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                products.add(ProductFactory.fromResultSet(rs));
            }
        }

        return products;
    }

    public List<Product> search(String keyword) throws SQLException {
        String sql = BASE_SELECT + """
                 WHERE LOWER(p.name) LIKE ? OR LOWER(p.barcode) LIKE ?
                 ORDER BY p.name
                """;
        String searchValue = "%" + keyword.toLowerCase() + "%";
        List<Product> products = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    products.add(ProductFactory.fromResultSet(rs));
                }
            }
        }

        return products;
    }

    public void insert(Product product) throws SQLException {
        String sql = """
                INSERT INTO products (barcode, name, category_id, price, stock, unit)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getBarcode());
            statement.setString(2, product.getName());
            statement.setInt(3, product.getCategoryId());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStock());
            statement.setString(6, product.getUnit());
            statement.executeUpdate();
        }
    }

    public void update(Product product) throws SQLException {
        String sql = """
                UPDATE products
                SET barcode = ?, name = ?, category_id = ?, price = ?, stock = ?, unit = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getBarcode());
            statement.setString(2, product.getName());
            statement.setInt(3, product.getCategoryId());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStock());
            statement.setString(6, product.getUnit());
            statement.setInt(7, product.getId());
            statement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}
