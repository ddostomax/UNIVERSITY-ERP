package edu.univ.erp.data;

import edu.univ.erp.models.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    private static final String SELECT_ALL =
            "SELECT course_id, code, title, credits FROM courses";
    
    private static final String INSERT_COURSE =
            "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
    
    private static final String SELECT_BY_ID =
            "SELECT course_id, code, title, credits FROM courses WHERE course_id = ?";

    public List<Course> findAll() throws SQLException {
        List<Course> list = new ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getLong("course_id"));
                c.setCode(rs.getString("code"));
                c.setTitle(rs.getString("title"));
                c.setCredits(rs.getInt("credits"));
                list.add(c);
            }
        }
        return list;
    }

    public Course findById(long courseId) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course c = new Course();
                    c.setCourseId(rs.getLong("course_id"));
                    c.setCode(rs.getString("code"));
                    c.setTitle(rs.getString("title"));
                    c.setCredits(rs.getInt("credits"));
                    return c;
                }
            }
        }
        return null;
    }

    public long createCourse(String code, String title, int credits) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_COURSE, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, title);
            ps.setInt(3, credits);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to create course");
    }
}


