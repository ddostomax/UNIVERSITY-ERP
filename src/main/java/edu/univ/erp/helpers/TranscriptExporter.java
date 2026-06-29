package edu.univ.erp.helpers;

import edu.univ.erp.data.GradeRepository;
import edu.univ.erp.data.StudentRepository;
import edu.univ.erp.models.StudentProfile;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting student transcripts to CSV and PDF formats.
 */
public class TranscriptExporter {

    /**
     * Export transcript to CSV format.
     */
    public static void exportToCSV(long studentUserId, String filePath, 
                                   List<GradeRepository.GradeWithCourse> grades) throws IOException {
        StudentRepository studentDao = new StudentRepository();
        StudentProfile student;
        try {
            student = studentDao.getStudentById(studentUserId);
        } catch (java.sql.SQLException e) {
            throw new IOException("Failed to load student information: " + e.getMessage(), e);
        }
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Header
            writer.append("UNIVERSITY TRANSCRIPT\n");
            if (student != null) {
                writer.append("Student Name: ").append(student.getName()).append("\n");
                writer.append("Roll No: ").append(student.getRollNo()).append("\n");
                writer.append("Program: ").append(student.getProgram() != null ? student.getProgram() : "").append("\n");
                writer.append("Year: ").append(String.valueOf(student.getYear())).append("\n");
            }
            writer.append("Generated: ").append(java.time.LocalDate.now().toString()).append("\n\n");
            
            // Column headers
            writer.append("Course Code,Course Title,Semester,Year,Component,Score,Final Grade\n");
            
            // Data rows
            for (GradeRepository.GradeWithCourse gwc : grades) {
                writer.append(escapeCSV(gwc.courseCode)).append(",");
                writer.append(escapeCSV(gwc.courseTitle)).append(",");
                writer.append(escapeCSV(gwc.semester != null ? gwc.semester : "")).append(",");
                writer.append(String.valueOf(gwc.year)).append(",");
                writer.append(escapeCSV(gwc.grade.getComponent())).append(",");
                writer.append(String.format("%.2f", gwc.grade.getScore())).append(",");
                writer.append(escapeCSV(gwc.grade.getFinalGrade() != null ? gwc.grade.getFinalGrade() : "")).append("\n");
            }
        }
    }

    /**
     * Export transcript to PDF format.
     */
    public static void exportToPDF(long studentUserId, String filePath,
                                   List<GradeRepository.GradeWithCourse> grades) throws IOException {
        StudentRepository studentDao = new StudentRepository();
        StudentProfile student;
        try {
            student = studentDao.getStudentById(studentUserId);
        } catch (java.sql.SQLException e) {
            throw new IOException("Failed to load student information: " + e.getMessage(), e);
        }
        
        com.lowagie.text.Document document = new com.lowagie.text.Document();
        try {
            com.lowagie.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(filePath));
            document.open();
            
            // Title
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("UNIVERSITY TRANSCRIPT", titleFont);
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Student info
            if (student != null) {
                com.lowagie.text.Font normalFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12);
                document.add(new com.lowagie.text.Paragraph("Student Name: " + student.getName(), normalFont));
                document.add(new com.lowagie.text.Paragraph("Roll No: " + student.getRollNo(), normalFont));
                if (student.getProgram() != null) {
                    document.add(new com.lowagie.text.Paragraph("Program: " + student.getProgram(), normalFont));
                }
                document.add(new com.lowagie.text.Paragraph("Year: " + student.getYear(), normalFont));
                document.add(new com.lowagie.text.Paragraph("Generated: " + java.time.LocalDate.now().toString(), normalFont));
                document.add(new com.lowagie.text.Paragraph(" "));
            }
            
            // Table
            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 3f, 1.5f, 1f, 1.5f, 1f, 1.5f});
            
            // Table headers
            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD);
            table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase("Course Code", headerFont)));
            table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase("Course Title", headerFont)));
            table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase("Semester", headerFont)));
            table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase("Year", headerFont)));
            table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase("Component", headerFont)));
            table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase("Score", headerFont)));
            table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase("Final Grade", headerFont)));
            
            // Table data
            com.lowagie.text.Font dataFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10);
            for (GradeRepository.GradeWithCourse gwc : grades) {
                table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(gwc.courseCode, dataFont)));
                table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(gwc.courseTitle, dataFont)));
                table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(gwc.semester != null ? gwc.semester : "", dataFont)));
                table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(String.valueOf(gwc.year), dataFont)));
                table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(gwc.grade.getComponent(), dataFont)));
                table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(String.format("%.2f", gwc.grade.getScore()), dataFont)));
                table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(gwc.grade.getFinalGrade() != null ? gwc.grade.getFinalGrade() : "", dataFont)));
            }
            
            document.add(table);
            
        } finally {
            document.close();
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

