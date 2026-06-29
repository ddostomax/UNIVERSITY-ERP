package edu.univ.erp.data;

import edu.univ.erp.models.StudentProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentRepository {

    private static final String SELECT_BY_USER_ID =
            "SELECT user_id, roll_no, name, program, year FROM students WHERE user_id = ?";
    
    private static final String INSERT_STUDENT =
            "INSERT INTO students (user_id, roll_no, name, program, year) VALUES (?, ?, ?, ?, ?)";

    public StudentProfile getStudentById(long userId) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USER_ID)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StudentProfile s = new StudentProfile();
                    s.setUserId(rs.getLong("user_id"));
                    s.setRollNo(rs.getString("roll_no"));
                    s.setName(rs.getString("name"));
                    s.setProgram(rs.getString("program"));
                    s.setYear(rs.getInt("year"));
                    return s;
                }
            }
        }
        return null;
    }

    public void insertStudent(long userId, String rollNo, String name, String program, Integer year) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_STUDENT)) {
            ps.setLong(1, userId);
            ps.setString(2, rollNo);
            ps.setString(3, name);
            ps.setString(4, program);
            if (year != null) {
                ps.setInt(5, year);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }
}


