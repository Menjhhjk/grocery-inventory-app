package com.example.grocery_inventory.repository;

import com.example.grocery_inventory.model.User;
import com.example.grocery_inventory.util.DatabaseUtil;
import com.example.grocery_inventory.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    public List<User> findAll() throws SQLException {
        String sql = "SELECT id, username, password_hash, COALESCE(role, 'VIEWER') AS role FROM users ORDER BY username";
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                users.add(fromResultSet(rs));
            }
        }

        return users;
    }

    public List<User> search(String keyword) throws SQLException {
        String sql = """
                SELECT id, username, password_hash, COALESCE(role, 'VIEWER') AS role
                FROM users
                WHERE LOWER(username) LIKE ? OR LOWER(COALESCE(role, 'VIEWER')) LIKE ?
                ORDER BY username
                """;
        String searchValue = "%" + keyword.toLowerCase() + "%";
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    users.add(fromResultSet(rs));
                }
            }
        }

        return users;
    }

    public Optional<User> findByUsernameAndPassword(String username, String plainPassword) throws SQLException {
        String sql = "SELECT id, username, password_hash, COALESCE(role, 'VIEWER') AS role FROM users WHERE username = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                String storedHash = rs.getString("password_hash");
                if (!PasswordUtil.verify(plainPassword, storedHash)) {
                    return Optional.empty();
                }

                return Optional.of(fromResultSet(rs));
            }
        }
    }

    public void createUser(String username, String plainPassword, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, PasswordUtil.hash(plainPassword));
            statement.setString(3, role);
            statement.executeUpdate();
        }
    }

    public void updateUser(User user, String plainPassword) throws SQLException {
        boolean updatePassword = plainPassword != null && !plainPassword.isBlank();
        String sql = updatePassword
                ? "UPDATE users SET username = ?, password_hash = ?, role = ? WHERE id = ?"
                : "UPDATE users SET username = ?, role = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            if (updatePassword) {
                statement.setString(2, PasswordUtil.hash(plainPassword));
                statement.setString(3, user.getRole());
                statement.setInt(4, user.getId());
            } else {
                statement.setString(2, user.getRole());
                statement.setInt(3, user.getId());
            }
            statement.executeUpdate();
        }
    }

    private User fromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("role")
        );
    }
}
