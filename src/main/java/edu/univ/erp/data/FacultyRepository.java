package edu.univ.erp.data;

import edu.univ.erp.models.InstructorProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FacultyRepository {

    private static final String SELECT_BY_USER_ID =
            "SELECT user_id, name, department FROM instructors WHERE user_id = ?";
    
    private static final String INSERT_INSTRUCTOR =
            "INSERT INTO instructors (user_id, name, department) VALUES (?, ?, ?)";
    
    private static final String SELECT_ALL =
            "SELECT user_id, name, department FROM instructors";

    public InstructorProfile findByUserId(long userId) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USER_ID)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    InstructorProfile profile = new InstructorProfile();
                    profile.setUserId(rs.getLong("user_id"));
                    profile.setName(rs.getString("name"));
                    profile.setDepartment(rs.getString("department"));
                    return profile;
                }
            }
        }
        return null;
    }

    public void createInstructor(long userId, String name, String department) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_INSTRUCTOR)) {
            ps.setLong(1, userId);
            ps.setString(2, name);
            ps.setString(3, department);
            ps.executeUpdate();
        }
    }

    public java.util.List<InstructorProfile> findAll() throws SQLException {
        java.util.List<InstructorProfile> list = new java.util.ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                InstructorProfile profile = new InstructorProfile();
                profile.setUserId(rs.getLong("user_id"));
                profile.setName(rs.getString("name"));
                profile.setDepartment(rs.getString("department"));
                list.add(profile);
            }
        }
        return list;
    }
}


