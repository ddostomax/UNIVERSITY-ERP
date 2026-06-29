package edu.univ.erp.data;

import edu.univ.erp.models.Section;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SectionRepository {

    private static final String SELECT_ALL =
            "SELECT section_id, course_id, instructor_id, day_of_week, start_time, end_time, room, capacity, semester, year, drop_deadline " +
            "FROM sections";

    private static final String SELECT_BY_ID =
            "SELECT section_id, course_id, instructor_id, day_of_week, start_time, end_time, room, capacity, semester, year, drop_deadline " +
                    "FROM sections WHERE section_id = ?";
    
    private static final String SELECT_BY_INSTRUCTOR =
            "SELECT s.section_id, s.course_id, s.instructor_id, s.day_of_week, s.start_time, s.end_time, " +
            "s.room, s.capacity, s.semester, s.year, s.drop_deadline, c.code, c.title, c.credits " +
            "FROM sections s " +
            "JOIN courses c ON s.course_id = c.course_id " +
            "WHERE s.instructor_id = ? " +
            "ORDER BY s.year DESC, s.semester, c.code";

    public List<Section> findAll() throws SQLException {
        List<Section> list = new ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Section findById(long sectionId) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setLong(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<SectionWithCourse> findByInstructor(long instructorId) throws SQLException {
        List<SectionWithCourse> results = new ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_INSTRUCTOR)) {
            ps.setLong(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SectionWithCourse swc = new SectionWithCourse();
                    swc.section = mapRow(rs);
                    swc.courseCode = rs.getString("code");
                    swc.courseTitle = rs.getString("title");
                    swc.credits = rs.getInt("credits");
                    results.add(swc);
                }
            }
        }
        return results;
    }

    private static final String INSERT_SECTION =
            "INSERT INTO sections (course_id, instructor_id, day_of_week, start_time, end_time, room, capacity, semester, year, drop_deadline) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_INSTRUCTOR =
            "UPDATE sections SET instructor_id = ? WHERE section_id = ?";

    private static final String UPDATE_SECTION =
            "UPDATE sections SET course_id = ?, instructor_id = ?, day_of_week = ?, start_time = ?, end_time = ?, " +
            "room = ?, capacity = ?, semester = ?, year = ?, drop_deadline = ? WHERE section_id = ?";

    public long createSection(long courseId, Long instructorId, String dayOfWeek, 
                             java.time.LocalTime startTime, java.time.LocalTime endTime,
                             String room, int capacity, String semester, int year, 
                             java.time.LocalDate dropDeadline) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SECTION, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, courseId);
            if (instructorId != null) {
                ps.setLong(2, instructorId);
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }
            ps.setString(3, dayOfWeek);
            if (startTime != null) {
                ps.setTime(4, java.sql.Time.valueOf(startTime));
            } else {
                ps.setNull(4, java.sql.Types.TIME);
            }
            if (endTime != null) {
                ps.setTime(5, java.sql.Time.valueOf(endTime));
            } else {
                ps.setNull(5, java.sql.Types.TIME);
            }
            ps.setString(6, room);
            ps.setInt(7, capacity);
            ps.setString(8, semester);
            ps.setInt(9, year);
            if (dropDeadline != null) {
                ps.setDate(10, java.sql.Date.valueOf(dropDeadline));
            } else {
                ps.setNull(10, java.sql.Types.DATE);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to create section");
    }

    public void assignInstructor(long sectionId, long instructorId) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_INSTRUCTOR)) {
            ps.setLong(1, instructorId);
            ps.setLong(2, sectionId);
            ps.executeUpdate();
        }
    }

    public void updateSection(long sectionId, long courseId, Long instructorId, String dayOfWeek,
                              LocalTime startTime, LocalTime endTime, String room, int capacity,
                              String semester, int year, java.time.LocalDate dropDeadline) throws SQLException {
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SECTION)) {
            ps.setLong(1, courseId);
            if (instructorId != null) {
                ps.setLong(2, instructorId);
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }
            ps.setString(3, dayOfWeek);
            if (startTime != null) {
                ps.setTime(4, java.sql.Time.valueOf(startTime));
            } else {
                ps.setNull(4, java.sql.Types.TIME);
            }
            if (endTime != null) {
                ps.setTime(5, java.sql.Time.valueOf(endTime));
            } else {
                ps.setNull(5, java.sql.Types.TIME);
            }
            ps.setString(6, room);
            ps.setInt(7, capacity);
            ps.setString(8, semester);
            ps.setInt(9, year);
            if (dropDeadline != null) {
                ps.setDate(10, java.sql.Date.valueOf(dropDeadline));
            } else {
                ps.setNull(10, java.sql.Types.DATE);
            }
            ps.setLong(11, sectionId);
            ps.executeUpdate();
        }
    }

    public static class SectionWithCourse {
        public Section section;
        public String courseCode;
        public String courseTitle;
        public int credits;
    }

    private Section mapRow(ResultSet rs) throws SQLException {
        Section s = new Section();
        s.setSectionId(rs.getLong("section_id"));
        s.setCourseId(rs.getLong("course_id"));
        long instId = rs.getLong("instructor_id");
        s.setInstructorId(rs.wasNull() ? null : instId);
        s.setDayOfWeek(rs.getString("day_of_week"));
        java.sql.Time start = rs.getTime("start_time");
        if (start != null) {
            s.setStartTime(start.toLocalTime());
        }
        java.sql.Time end = rs.getTime("end_time");
        if (end != null) {
            s.setEndTime(end.toLocalTime());
        }
        s.setRoom(rs.getString("room"));
        s.setCapacity(rs.getInt("capacity"));
        s.setSemester(rs.getString("semester"));
        s.setYear(rs.getInt("year"));
        java.sql.Date dropDeadline = rs.getDate("drop_deadline");
        if (dropDeadline != null) {
            s.setDropDeadline(dropDeadline.toLocalDate());
        }
        return s;
    }
}


