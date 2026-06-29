package edu.univ.erp.screens.student;

import edu.univ.erp.api.catalog.CatalogApi;
import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.api.types.SectionCatalogRow;
import edu.univ.erp.api.types.StudentRegistrationRow;
import edu.univ.erp.data.GradeRepository;
import edu.univ.erp.screens.common.MaintenanceBanner;
import edu.univ.erp.helpers.TranscriptExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentDashboard extends JPanel {

    private final StudentApi studentApi = new StudentApi();
    private final CatalogApi catalogApi = new CatalogApi();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JTable catalogTable = new JTable();
    private final JTable registrationsTable = new JTable();
    private final JTable timetableTable = new JTable();
    private final JTable gradesTable = new JTable();
    private final MaintenanceBanner maintenanceBanner = new MaintenanceBanner();

    public StudentDashboard() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x1E, 0x1E, 0x24));
        add(maintenanceBanner, BorderLayout.NORTH);

        tabbedPane.addTab("Course Catalog", createCatalogPanel());
        tabbedPane.addTab("My Registrations", createRegistrationsPanel());
        tabbedPane.addTab("Timetable", createTimetablePanel());
        tabbedPane.addTab("Grades", createGradesPanel());
        tabbedPane.addTab("Transcript", createTranscriptPanel());

        styleTabbedPane(tabbedPane);

        add(tabbedPane, BorderLayout.CENTER);

        refreshAll();
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        catalogTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(catalogTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        JButton registerBtn = new JButton("Register for Selected Section");
        buttons.add(refreshBtn);
        buttons.add(registerBtn);

        refreshBtn.addActionListener(e -> refreshCatalog());
        registerBtn.addActionListener(e -> registerSelectedSection());

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRegistrationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));
        registrationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        registrationsTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(registrationsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        JButton dropBtn = new JButton("Drop Selected Section");
        buttons.add(refreshBtn);
        buttons.add(dropBtn);

        refreshBtn.addActionListener(e -> refreshRegistrations());
        dropBtn.addActionListener(e -> dropSelectedSection());

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));
        timetableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timetableTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(timetableTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        buttons.add(refreshBtn);
        refreshBtn.addActionListener(e -> refreshTimetable());

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));
        gradesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradesTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        buttons.add(refreshBtn);
        refreshBtn.addActionListener(e -> refreshGrades());

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTranscriptPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));
        JLabel info = new JLabel("<html><center>Download your transcript as CSV or PDF.<br/>Click the buttons below.</center></html>", SwingConstants.CENTER);
        info.setForeground(new Color(0xA0, 0xA0, 0xB3));
        panel.add(info, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton csvBtn = new JButton("Download CSV");
        JButton pdfBtn = new JButton("Download PDF");
        buttons.add(csvBtn);
        buttons.add(pdfBtn);

        csvBtn.addActionListener(e -> exportTranscriptCSV());
        pdfBtn.addActionListener(e -> exportTranscriptPDF());

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAll() {
        refreshCatalog();
        refreshRegistrations();
        refreshTimetable();
        refreshGrades();
        maintenanceBanner.refresh();
    }

    private void refreshCatalog() {
        ApiResponse<List<SectionCatalogRow>> response = catalogApi.listCatalog();
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] columns = {"Section ID", "Course Code", "Title", "Credits", "Day", "Time", "Room", "Capacity", "Enrolled", "Instructor", "Semester", "Year"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (SectionCatalogRow row : response.getData()) {
            String timeStr = "";
            if (row.getStartTime() != null && row.getEndTime() != null) {
                timeStr = row.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                          row.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            model.addRow(new Object[]{
                row.getSectionId(),
                row.getCourseCode(),
                row.getCourseTitle(),
                row.getCredits(),
                row.getDayOfWeek(),
                timeStr,
                row.getRoom(),
                row.getCapacity(),
                row.getEnrolled(),
                row.getInstructorName() != null ? row.getInstructorName() : "TBA",
                row.getSemester(),
                row.getYear()
            });
        }

        catalogTable.setModel(model);
        styleTableDark(catalogTable);
    }

    private void refreshRegistrations() {
        ApiResponse<List<StudentRegistrationRow>> response = studentApi.myRegistrations();
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] columns = {"Section ID", "Course Code", "Title", "Day", "Time", "Room", "Semester", "Year"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (StudentRegistrationRow row : response.getData()) {
            String timeStr = "";
            if (row.getStartTime() != null && row.getEndTime() != null) {
                timeStr = row.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                          row.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            model.addRow(new Object[]{
                row.getSectionId(),
                row.getCourseCode(),
                row.getCourseTitle(),
                row.getDayOfWeek(),
                timeStr,
                row.getRoom(),
                row.getSemester(),
                row.getYear()
            });
        }

        registrationsTable.setModel(model);
        styleTableDark(registrationsTable);
    }

    private void refreshTimetable() {
        ApiResponse<List<StudentRegistrationRow>> response = studentApi.getTimetable();
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] columns = {"Course Code", "Title", "Day", "Time", "Room"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (StudentRegistrationRow row : response.getData()) {
            String timeStr = "";
            if (row.getStartTime() != null && row.getEndTime() != null) {
                timeStr = row.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                          row.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            model.addRow(new Object[]{
                row.getCourseCode(),
                row.getCourseTitle(),
                row.getDayOfWeek(),
                timeStr,
                row.getRoom()
            });
        }

        timetableTable.setModel(model);
        styleTableDark(timetableTable);
    }

    private void refreshGrades() {
        ApiResponse<List<GradeRepository.GradeWithCourse>> response = studentApi.getGrades();
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] columns = {"Course Code", "Title", "Semester", "Year", "Component", "Score", "Final Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (GradeRepository.GradeWithCourse gwc : response.getData()) {
            model.addRow(new Object[]{
                gwc.courseCode,
                gwc.courseTitle,
                gwc.semester,
                gwc.year,
                gwc.grade.getComponent(),
                String.format("%.2f", gwc.grade.getScore()),
                gwc.grade.getFinalGrade() != null ? gwc.grade.getFinalGrade() : "-"
            });
        }

        gradesTable.setModel(model);
        styleTableDark(gradesTable);
    }

    private void registerSelectedSection() {
        int row = catalogTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a section to register for.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = catalogTable.convertRowIndexToModel(row);
        Long sectionId = (Long) catalogTable.getModel().getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Register for this section?", "Confirm Registration", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        ApiResponse<Void> response = studentApi.register(sectionId);
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAll();
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dropSelectedSection() {
        int row = registrationsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a section to drop.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = registrationsTable.convertRowIndexToModel(row);
        Long sectionId = (Long) registrationsTable.getModel().getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Drop this section?", "Confirm Drop", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        ApiResponse<Void> response = studentApi.drop(sectionId);
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAll();
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Drop Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportTranscriptCSV() {
        ApiResponse<List<GradeRepository.GradeWithCourse>> response = studentApi.getGrades();
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Transcript as CSV");
        chooser.setSelectedFile(new java.io.File("transcript.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = chooser.getSelectedFile();
                String filePath = file.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath += ".csv";
                }
                TranscriptExporter.exportToCSV(
                    edu.univ.erp.security.session.SessionManager.getCurrentSession().getUserId(),
                    filePath,
                    response.getData()
                );
                JOptionPane.showMessageDialog(this, "Transcript exported successfully to:\n" + filePath, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to export transcript: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportTranscriptPDF() {
        ApiResponse<List<GradeRepository.GradeWithCourse>> response = studentApi.getGrades();
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Transcript as PDF");
        chooser.setSelectedFile(new java.io.File("transcript.pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = chooser.getSelectedFile();
                String filePath = file.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }
                TranscriptExporter.exportToPDF(
                    edu.univ.erp.security.session.SessionManager.getCurrentSession().getUserId(),
                    filePath,
                    response.getData()
                );
                JOptionPane.showMessageDialog(this, "Transcript exported successfully to:\n" + filePath, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to export transcript: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleTabbedPane(JTabbedPane tabs) {
        tabs.setBackground(new Color(0x1E, 0x1E, 0x24));
        tabs.setForeground(new Color(0xA0, 0xA0, 0xB3));
    }

    private void styleTableDark(JTable table) {
        table.setBackground(new Color(0x12, 0x12, 0x16));
        table.setForeground(new Color(0xF5, 0xF5, 0xF7));
        table.setGridColor(new Color(0x24, 0x24, 0x30));
        table.setSelectionBackground(new Color(0x38, 0x37, 0x9F));
        table.setSelectionForeground(new Color(0xF5, 0xF5, 0xF7));
        table.setRowHeight(24);
    }
}

