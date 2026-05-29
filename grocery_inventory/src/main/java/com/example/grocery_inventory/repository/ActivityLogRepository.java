package com.example.grocery_inventory.repository;

import com.example.grocery_inventory.model.ActivityLog;
import com.example.grocery_inventory.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogRepository {
    private static final String BASE_SELECT = """
            SELECT action, entity, description, performed_by,
                   TO_CHAR(performed_at, 'YYYY-MM-DD HH24:MI:SS') AS performed_at
            FROM activity_log
            """;

    public void log(String action, String entity, String description, String performedBy) {
        String sql = """
                INSERT INTO activity_log (action, entity, description, performed_by)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, action);
            statement.setString(2, entity);
            statement.setString(3, description);
            statement.setString(4, performedBy);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Failed to write activity log: " + ex.getMessage());
        }
    }

    public List<ActivityLog> findAll() throws SQLException {
        String sql = BASE_SELECT + " ORDER BY performed_at DESC";
        List<ActivityLog> logs = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                logs.add(fromResultSet(rs));
            }
        }

        return logs;
    }

    public List<ActivityLog> findByEntity(String entity) throws SQLException {
        String sql = BASE_SELECT + " WHERE entity = ? ORDER BY performed_at DESC";
        List<ActivityLog> logs = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    logs.add(fromResultSet(rs));
                }
            }
        }

        return logs;
    }

    private ActivityLog fromResultSet(ResultSet rs) throws SQLException {
        return new ActivityLog(
                rs.getString("action"),
                rs.getString("entity"),
                rs.getString("description"),
                rs.getString("performed_by"),
                rs.getString("performed_at")
        );
    }
}
