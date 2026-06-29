package edu.univ.erp.data;

import edu.univ.erp.api.types.StudentRegistrationRow;
import edu.univ.erp.models.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentRepository {

    private static final String SELECT_BY_STUDENT =
            "SELECT enrollment_id, student_id, section_id, status, enrollment_date FROM enrollments WHERE student_id = ?";

    private static final String SELECT_BY_STUDENT_AND_SECTION =
            "SELECT enrollment_id, student_id, section_id, status, enrollment_date FROM enrollments WHERE student_id = ? AND section_id = ?";

    private static final String INSERT_ENROLLMENT =
            "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, ?)";

    private static final String UPDATE_STATUS =
            "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";

    private static final String COUNT_ENROLLED_IN_SECTION =
            "SELECT COUNT(*) FROM enrollments WHERE section_id = ? AND status = 'ENROLLED'";

    private static final String SELECT_REGISTRATION_ROWS =
            "SELECT e.enrollment_id, e.section_id, c.code, c.title, s.day_of_week, s.start_time, s.end_time, " +
                    "s.room, s.semester, s.year, e.enrollment_date " +
                    "FROM enrollments e " +
                    "JOIN sections s ON e.section_id = s.section_id " +
                    "JOIN courses c ON s.course_id = c.course_id " +
                    "WHERE e.student_id = ? AND e.status = 'ENROLLED'";
    
    private static final String SELECT_BY_SECTION =
            "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrollment_date, " +
            "st.name as student_name, st.roll_no " +
            "FROM enrollments e " +
            "JOIN students st ON e.student_id = st.user_id " +
            "WHERE e.section_id = ? AND e.status = 'ENROLLED' " +
            "ORDER BY st.roll_no";

    public List<Enrollment> findByStudent(long studentId) throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STUDENT)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public Enrollment lookupEnrollment(long studentId, long sectionId) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STUDENT_AND_SECTION)) {
            ps.setLong(1, studentId);
            ps.setLong(2, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Enrollment recordEnrollment(long studentId, long sectionId, String status) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_ENROLLMENT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, studentId);
            ps.setLong(2, sectionId);
            ps.setString(3, status);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    Enrollment e = new Enrollment();
                    e.setEnrollmentId(keys.getLong(1));
                    e.setStudentId(studentId);
                    e.setSectionId(sectionId);
                    e.setStatus(status);
                    return e;
                }
            }
        }
        return null;
    }

    public void updateStatus(long enrollmentId, String status) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {
            ps.setString(1, status);
            ps.setLong(2, enrollmentId);
            ps.executeUpdate();
        }
    }

    public int countEnrolledInSection(long sectionId) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(COUNT_ENROLLED_IN_SECTION)) {
            ps.setLong(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<StudentRegistrationRow> findRegistrationRows(long studentId) throws SQLException {
        List<StudentRegistrationRow> rows = new ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_REGISTRATION_ROWS)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentRegistrationRow row = new StudentRegistrationRow();
                    row.setEnrollmentId(rs.getLong("enrollment_id"));
                    row.setSectionId(rs.getLong("section_id"));
                    row.setCourseCode(rs.getString("code"));
                    row.setCourseTitle(rs.getString("title"));
                    row.setDayOfWeek(rs.getString("day_of_week"));
                    java.sql.Time start = rs.getTime("start_time");
                    if (start != null) {
                        row.setStartTime(start.toLocalTime());
                    }
                    java.sql.Time end = rs.getTime("end_time");
                    if (end != null) {
                        row.setEndTime(end.toLocalTime());
                    }
                    row.setRoom(rs.getString("room"));
                    row.setSemester(rs.getString("semester"));
                    row.setYear(rs.getInt("year"));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    public List<EnrollmentWithStudent> findBySection(long sectionId) throws SQLException {
        List<EnrollmentWithStudent> results = new ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_SECTION)) {
            ps.setLong(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EnrollmentWithStudent ews = new EnrollmentWithStudent();
                    ews.enrollment = mapRow(rs);
                    ews.studentName = rs.getString("student_name");
                    ews.rollNo = rs.getString("roll_no");
                    results.add(ews);
                }
            }
        }
        return results;
    }

    private Enrollment mapRow(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(rs.getLong("enrollment_id"));
        e.setStudentId(rs.getLong("student_id"));
        e.setSectionId(rs.getLong("section_id"));
        e.setStatus(rs.getString("status"));
        java.sql.Date enrollmentDate = rs.getDate("enrollment_date");
        if (enrollmentDate != null) {
            e.setEnrollmentDate(enrollmentDate.toLocalDate());
        }
        return e;
    }

    public static class EnrollmentWithStudent {
        public Enrollment enrollment;
        public String studentName;
        public String rollNo;
    }
}


