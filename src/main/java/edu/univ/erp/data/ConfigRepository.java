package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfigRepository {

    private static final String SELECT_VALUE =
            "SELECT value FROM settings WHERE `key` = ?";

    private static final String INSERT_OR_UPDATE =
            "INSERT INTO settings (`key`, `value`) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)";

    public String readSetting(String key) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_VALUE)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        }
        return null;
    }

    public void changeSetting(String key, String value) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_OR_UPDATE)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        }
    }
}


