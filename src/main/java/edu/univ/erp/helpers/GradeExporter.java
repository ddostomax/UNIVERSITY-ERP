package edu.univ.erp.helpers;

import edu.univ.erp.data.GradeRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for exporting instructor grades to CSV format.
 */
public class GradeExporter {

    /**
     * Export grades for a section to CSV format.
     */
    public static void exportToCSV(String filePath, List<GradeRepository.GradeWithStudent> grades) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Header
            writer.append("GRADE SHEET\n");
            writer.append("Generated: ").append(java.time.LocalDate.now().toString()).append("\n\n");
            
            // Column headers
            writer.append("Roll No,Student Name,Enrollment ID,Component,Score,Final Grade\n");
            
            // Data rows
            for (GradeRepository.GradeWithStudent gws : grades) {
                writer.append(escapeCSV(gws.rollNo != null ? gws.rollNo : "")).append(",");
                writer.append(escapeCSV(gws.studentName != null ? gws.studentName : "")).append(",");
                long enrollmentId = gws.grade.getEnrollmentId();
                writer.append(enrollmentId > 0 ? String.valueOf(enrollmentId) : "").append(",");
                String component = gws.grade.getComponent();
                writer.append(escapeCSV((component != null && !component.isEmpty()) ? component : "")).append(",");
                double score = gws.grade.getScore();
                if (component != null && !component.isEmpty() && score >= 0) {
                    writer.append(String.format("%.2f", score));
                } else {
                    writer.append("");
                }
                writer.append(",");
                writer.append(escapeCSV(gws.grade.getFinalGrade() != null ? gws.grade.getFinalGrade() : "")).append("\n");
            }
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

