package edu.univ.erp.data;

import edu.univ.erp.models.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeRepository {

    private static final String SELECT_BY_ENROLLMENT =
            "SELECT grade_id, enrollment_id, component, score, final_grade " +
            "FROM grades WHERE enrollment_id = ? ORDER BY component";

    private static final String SELECT_BY_STUDENT =
            "SELECT g.grade_id, g.enrollment_id, g.component, g.score, g.final_grade, " +
            "c.code as course_code, c.title as course_title, s.semester, s.year " +
            "FROM grades g " +
            "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
            "JOIN sections s ON e.section_id = s.section_id " +
            "JOIN courses c ON s.course_id = c.course_id " +
            "WHERE e.student_id = ? AND e.status = 'ENROLLED' " +
            "ORDER BY s.year DESC, s.semester, c.code";

    public List<Grade> findByEnrollment(long enrollmentId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ENROLLMENT)) {
            ps.setLong(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapRow(rs));
                }
            }
        }
        return grades;
    }

    public List<GradeWithCourse> findByStudent(long studentId) throws SQLException {
        List<GradeWithCourse> results = new ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STUDENT)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GradeWithCourse gwc = new GradeWithCourse();
                    gwc.grade = mapRow(rs);
                    gwc.courseCode = rs.getString("course_code");
                    gwc.courseTitle = rs.getString("course_title");
                    gwc.semester = rs.getString("semester");
                    gwc.year = rs.getInt("year");
                    results.add(gwc);
                }
            }
        }
        return results;
    }

    private Grade mapRow(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setGradeId(rs.getLong("grade_id"));
        g.setEnrollmentId(rs.getLong("enrollment_id"));
        g.setComponent(rs.getString("component"));
        g.setScore(rs.getDouble("score"));
        String finalGrade = rs.getString("final_grade");
        if (finalGrade != null) {
            g.setFinalGrade(finalGrade);
        }
        return g;
    }

    private static final String INSERT_GRADE =
            "INSERT INTO grades (enrollment_id, component, score) VALUES (?, ?, ?)";
    
    private static final String UPDATE_GRADE_SCORE =
            "UPDATE grades SET score = ? WHERE enrollment_id = ? AND component = ?";
    
    private static final String SELECT_GRADE_BY_ENROLLMENT_AND_COMPONENT =
            "SELECT grade_id, enrollment_id, component, score, final_grade " +
            "FROM grades WHERE enrollment_id = ? AND component = ?";

    private static final String UPDATE_FINAL_GRADE =
            "UPDATE grades SET final_grade = ? WHERE enrollment_id = ?";

    private static final String SELECT_BY_SECTION =
            "SELECT DISTINCT e.enrollment_id, e.student_id, st.name as student_name, st.roll_no, " +
            "c.code as course_code, c.title as course_title " +
            "FROM enrollments e " +
            "JOIN students st ON e.student_id = st.user_id " +
            "JOIN sections s ON e.section_id = s.section_id " +
            "JOIN courses c ON s.course_id = c.course_id " +
            "WHERE s.section_id = ? AND e.status = 'ENROLLED' " +
            "ORDER BY st.roll_no";
    
    private static final String SELECT_GRADES_FOR_ENROLLMENT =
            "SELECT grade_id, enrollment_id, component, score, final_grade " +
            "FROM grades WHERE enrollment_id = ? ORDER BY component";

    public void insertOrUpdateGrade(long enrollmentId, String component, double score) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection()) {
            // Check if grade exists
            try (PreparedStatement ps = conn.prepareStatement(SELECT_GRADE_BY_ENROLLMENT_AND_COMPONENT)) {
                ps.setLong(1, enrollmentId);
                ps.setString(2, component);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Update existing
                        try (PreparedStatement updatePs = conn.prepareStatement(UPDATE_GRADE_SCORE)) {
                            updatePs.setDouble(1, score);
                            updatePs.setLong(2, enrollmentId);
                            updatePs.setString(3, component);
                            updatePs.executeUpdate();
                        }
                    } else {
                        // Insert new
                        try (PreparedStatement insertPs = conn.prepareStatement(INSERT_GRADE)) {
                            insertPs.setLong(1, enrollmentId);
                            insertPs.setString(2, component);
                            insertPs.setDouble(3, score);
                            insertPs.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    public void updateFinalGrade(long enrollmentId, String finalGrade) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_FINAL_GRADE)) {
            ps.setString(1, finalGrade);
            ps.setLong(2, enrollmentId);
            ps.executeUpdate();
        }
    }

    public List<GradeWithStudent> findBySection(long sectionId) throws SQLException {
        List<GradeWithStudent> results = new ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_SECTION)) {
            ps.setLong(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long enrollmentId = rs.getLong("enrollment_id");
                    
                    // Get all grades for this enrollment
                    List<Grade> grades = findByEnrollment(enrollmentId);
                    
                    if (grades.isEmpty()) {
                        // Student has no grades yet - show them once with empty grade
                        GradeWithStudent gws = new GradeWithStudent();
                        Grade placeholder = new Grade();
                        placeholder.setEnrollmentId(enrollmentId);
                        placeholder.setComponent("");
                        placeholder.setScore(0);
                        placeholder.setFinalGrade(null);
                        gws.grade = placeholder;
                        gws.studentId = rs.getLong("student_id");
                        gws.studentName = rs.getString("student_name");
                        gws.rollNo = rs.getString("roll_no");
                        gws.courseCode = rs.getString("course_code");
                        gws.courseTitle = rs.getString("course_title");
                        results.add(gws);
                    } else {
                        // Student has grades - show each grade component
                        for (Grade grade : grades) {
                            GradeWithStudent gws = new GradeWithStudent();
                            gws.grade = grade;
                            gws.studentId = rs.getLong("student_id");
                            gws.studentName = rs.getString("student_name");
                            gws.rollNo = rs.getString("roll_no");
                            gws.courseCode = rs.getString("course_code");
                            gws.courseTitle = rs.getString("course_title");
                            results.add(gws);
                        }
                    }
                }
            }
        }
        return results;
    }

    public static class GradeWithCourse {
        public Grade grade;
        public String courseCode;
        public String courseTitle;
        public String semester;
        public int year;
    }

    public static class GradeWithStudent {
        public Grade grade;
        public long studentId;
        public String studentName;
        public String rollNo;
        public String courseCode;
        public String courseTitle;
    }
}

