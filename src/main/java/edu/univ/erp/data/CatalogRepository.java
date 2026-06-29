package edu.univ.erp.data;

import edu.univ.erp.api.types.SectionCatalogRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogRepository {

    private static final String CATALOG_SQL =
            "SELECT s.section_id, c.code, c.title, c.credits, s.capacity, s.semester, s.year, " +
                    "s.day_of_week, s.start_time, s.end_time, s.room, i.name AS instructor_name, " +
                    "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status = 'ENROLLED') AS enrolled_count " +
                    "FROM sections s " +
                    "JOIN courses c ON s.course_id = c.course_id " +
                    "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                    "ORDER BY c.code, s.section_id";

    public List<SectionCatalogRow> listCatalog() throws SQLException {
        List<SectionCatalogRow> rows = new ArrayList<>();
        try (Connection conn = DBConnector.getErpDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(CATALOG_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SectionCatalogRow row = new SectionCatalogRow();
                row.setSectionId(rs.getLong("section_id"));
                row.setCourseCode(rs.getString("code"));
                row.setCourseTitle(rs.getString("title"));
                row.setCredits(rs.getInt("credits"));
                row.setCapacity(rs.getInt("capacity"));
                row.setEnrolled(rs.getInt("enrolled_count"));
                row.setSemester(rs.getString("semester"));
                row.setYear(rs.getInt("year"));
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
                row.setInstructorName(rs.getString("instructor_name"));
                rows.add(row);
            }
        }
        return rows;
    }
}


