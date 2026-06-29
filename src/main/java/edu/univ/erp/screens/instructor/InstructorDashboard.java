package edu.univ.erp.screens.instructor;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.data.GradeRepository;
import edu.univ.erp.data.SectionRepository;
import edu.univ.erp.core.FacultyOperations.ClassStats;
import edu.univ.erp.screens.common.MaintenanceBanner;
import edu.univ.erp.helpers.GradeExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructorDashboard extends JPanel {

    private final InstructorApi instructorApi = new InstructorApi();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JTable sectionsTable = new JTable();
    private final JTable gradesTable = new JTable();
    private final JTable statsTable = new JTable();
    private final MaintenanceBanner maintenanceBanner = new MaintenanceBanner();
    private long selectedSectionId = -1;

    public InstructorDashboard() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x1E, 0x1E, 0x24));
        add(maintenanceBanner, BorderLayout.NORTH);

        tabbedPane.addTab("My Sections", createSectionsPanel());
        tabbedPane.addTab("Grade Entry", createGradeEntryPanel());
        tabbedPane.addTab("Statistics", createStatisticsPanel());

        styleTabbedPane(tabbedPane);

        add(tabbedPane, BorderLayout.CENTER);

        refreshAll();
    }

    private JPanel createSectionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));
        sectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionsTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        JButton viewGradesBtn = new JButton("View Grades for Selected Section");
        buttons.add(refreshBtn);
        buttons.add(viewGradesBtn);

        refreshBtn.addActionListener(e -> refreshSections());
        viewGradesBtn.addActionListener(e -> {
            int row = sectionsTable.getSelectedRow();
            if (row >= 0) {
                int modelRow = sectionsTable.convertRowIndexToModel(row);
                selectedSectionId = (Long) sectionsTable.getModel().getValueAt(modelRow, 0);
                tabbedPane.setSelectedIndex(1);
                refreshGrades();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a section first.");
            }
        });

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createGradeEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        JLabel sectionLabel = new JLabel("Section: Not selected");
        sectionLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        JButton selectSectionBtn = new JButton("Select Section");
        topPanel.add(sectionLabel);
        topPanel.add(selectSectionBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        gradesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradesTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        JPanel entryPanel = new JPanel(new GridBagLayout());
        entryPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel componentLabel = new JLabel("Component:");
        componentLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        entryPanel.add(componentLabel, gbc);
        gbc.gridx = 1;
        JComboBox<String> componentCombo = new JComboBox<>(new String[]{"QUIZ", "MIDTERM", "END_SEM"});
        entryPanel.add(componentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel scoreLabel = new JLabel("Score (0-100):");
        scoreLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        entryPanel.add(scoreLabel, gbc);
        gbc.gridx = 1;
        JTextField scoreField = new JTextField(10);
        entryPanel.add(scoreField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton enterScoreBtn = new JButton("Enter Score");
        entryPanel.add(enterScoreBtn, gbc);

        bottomPanel.add(entryPanel, BorderLayout.NORTH);

        JPanel computePanel = new JPanel(new FlowLayout());
        computePanel.setOpaque(false);
        JLabel quizWeightLabel = new JLabel("Quiz Weight:");
        quizWeightLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        JTextField quizWeightField = new JTextField("20", 5);
        JLabel midtermWeightLabel = new JLabel("Midterm Weight:");
        midtermWeightLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        JTextField midtermWeightField = new JTextField("30", 5);
        JLabel endSemWeightLabel = new JLabel("End-Sem Weight:");
        endSemWeightLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        JTextField endSemWeightField = new JTextField("50", 5);
        JButton computeBtn = new JButton("Compute Final Grades");
        computePanel.add(quizWeightLabel);
        computePanel.add(quizWeightField);
        computePanel.add(midtermWeightLabel);
        computePanel.add(midtermWeightField);
        computePanel.add(endSemWeightLabel);
        computePanel.add(endSemWeightField);
        computePanel.add(computeBtn);
        bottomPanel.add(computePanel, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        JButton exportBtn = new JButton("Export to CSV");
        buttons.add(refreshBtn);
        buttons.add(exportBtn);
        bottomPanel.add(buttons, BorderLayout.SOUTH);

        selectSectionBtn.addActionListener(e -> {
            int row = sectionsTable.getSelectedRow();
            if (row >= 0) {
                int modelRow = sectionsTable.convertRowIndexToModel(row);
                selectedSectionId = (Long) sectionsTable.getModel().getValueAt(modelRow, 0);
                sectionLabel.setText("Section: " + sectionsTable.getModel().getValueAt(modelRow, 1));
                refreshGrades();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a section from 'My Sections' tab first.");
            }
        });

        enterScoreBtn.addActionListener(e -> {
            if (selectedSectionId < 0) {
                JOptionPane.showMessageDialog(this, "Please select a section first.");
                return;
            }
            int row = gradesTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a student.");
                return;
            }
            int modelRow = gradesTable.convertRowIndexToModel(row);
            long enrollmentId = (Long) gradesTable.getModel().getValueAt(modelRow, 0);
            String component = (String) componentCombo.getSelectedItem();
            try {
                double score = Double.parseDouble(scoreField.getText());
                ApiResponse<Void> response = instructorApi.enterScore(selectedSectionId, enrollmentId, component, score);
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    scoreField.setText("");
                    refreshGrades();
                } else {
                    JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid score. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        computeBtn.addActionListener(e -> {
            if (selectedSectionId < 0) {
                JOptionPane.showMessageDialog(this, "Please select a section first.");
                return;
            }
            try {
                Map<String, Double> weights = new HashMap<>();
                weights.put("QUIZ", Double.parseDouble(quizWeightField.getText()));
                weights.put("MIDTERM", Double.parseDouble(midtermWeightField.getText()));
                weights.put("END_SEM", Double.parseDouble(endSemWeightField.getText()));
                
                ApiResponse<Void> response = instructorApi.computeFinalGrades(selectedSectionId, weights);
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    refreshGrades();
                } else {
                    JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid weights. Please enter numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshBtn.addActionListener(e -> refreshGrades());
        exportBtn.addActionListener(e -> exportGradesToCSV());

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        JLabel sectionLabel = new JLabel("Section: Not selected");
        sectionLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        JButton selectSectionBtn = new JButton("Select Section");
        topPanel.add(sectionLabel);
        topPanel.add(selectSectionBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        statsTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(statsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        buttons.add(refreshBtn);

        selectSectionBtn.addActionListener(e -> {
            int row = sectionsTable.getSelectedRow();
            if (row >= 0) {
                int modelRow = sectionsTable.convertRowIndexToModel(row);
                selectedSectionId = (Long) sectionsTable.getModel().getValueAt(modelRow, 0);
                sectionLabel.setText("Section: " + sectionsTable.getModel().getValueAt(modelRow, 1));
                refreshStats();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a section from 'My Sections' tab first.");
            }
        });

        refreshBtn.addActionListener(e -> refreshStats());

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAll() {
        refreshSections();
    }

    private void refreshSections() {
        ApiResponse<List<SectionRepository.SectionWithCourse>> response = instructorApi.getMySections();
        if (response.isSuccess()) {
            List<SectionRepository.SectionWithCourse> sections = response.getData();
            String[] columns = {"Section ID", "Course Code", "Course Title", "Credits", "Day", "Time", "Room", "Capacity", "Semester", "Year"};
            Object[][] data = new Object[sections.size()][columns.length];
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            for (int i = 0; i < sections.size(); i++) {
                SectionRepository.SectionWithCourse swc = sections.get(i);
                data[i][0] = swc.section.getSectionId();
                data[i][1] = swc.courseCode;
                data[i][2] = swc.courseTitle;
                data[i][3] = swc.credits;
                data[i][4] = swc.section.getDayOfWeek();
                if (swc.section.getStartTime() != null && swc.section.getEndTime() != null) {
                    data[i][5] = swc.section.getStartTime().format(timeFormatter) + "-" + swc.section.getEndTime().format(timeFormatter);
                } else {
                    data[i][5] = "";
                }
                data[i][6] = swc.section.getRoom();
                data[i][7] = swc.section.getCapacity();
                data[i][8] = swc.section.getSemester();
                data[i][9] = swc.section.getYear();
            }
            
            sectionsTable.setModel(new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            styleTableDark(sectionsTable);
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshGrades() {
        if (selectedSectionId < 0) {
            gradesTable.setModel(new DefaultTableModel(new Object[0][0], new String[]{}));
            return;
        }
        
        ApiResponse<List<GradeRepository.GradeWithStudent>> response = instructorApi.getGradesForSection(selectedSectionId);
        if (response.isSuccess()) {
            List<GradeRepository.GradeWithStudent> grades = response.getData();
            String[] columns = {"Enrollment ID", "Roll No", "Student Name", "Component", "Score", "Final Grade"};
            Object[][] data = new Object[grades.size()][columns.length];
            
            for (int i = 0; i < grades.size(); i++) {
                GradeRepository.GradeWithStudent gws = grades.get(i);
                long enrollmentId = gws.grade.getEnrollmentId();
                data[i][0] = enrollmentId > 0 ? enrollmentId : "-";
                data[i][1] = gws.rollNo != null ? gws.rollNo : "";
                data[i][2] = gws.studentName != null ? gws.studentName : "";
                String component = gws.grade.getComponent();
                data[i][3] = (component != null && !component.isEmpty()) ? component : "-";
                double score = gws.grade.getScore();
                data[i][4] = (component != null && !component.isEmpty() && score >= 0) ? String.format("%.2f", score) : "-";
                data[i][5] = gws.grade.getFinalGrade() != null ? gws.grade.getFinalGrade() : "-";
            }
            
            gradesTable.setModel(new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshStats() {
        if (selectedSectionId < 0) {
            statsTable.setModel(new DefaultTableModel(new Object[0][0], new String[]{}));
            return;
        }
        
        ApiResponse<ClassStats> response = instructorApi.getClassStats(selectedSectionId);
        if (response.isSuccess()) {
            ClassStats stats = response.getData();
            String[] columns = {"Component", "Average", "Min", "Max"};
            Object[][] data = new Object[stats.componentAverages.size()][columns.length];
            
            int i = 0;
            for (Map.Entry<String, Double> entry : stats.componentAverages.entrySet()) {
                String component = entry.getKey();
                data[i][0] = component;
                data[i][1] = String.format("%.2f", entry.getValue());
                data[i][2] = String.format("%.2f", stats.componentMins.get(component));
                data[i][3] = String.format("%.2f", stats.componentMaxs.get(component));
                i++;
            }
            
            statsTable.setModel(new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportGradesToCSV() {
        if (selectedSectionId < 0) {
            JOptionPane.showMessageDialog(this, "Please select a section first.");
            return;
        }
        
        ApiResponse<List<GradeRepository.GradeWithStudent>> response = instructorApi.getGradesForSection(selectedSectionId);
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Grades to CSV");
        fileChooser.setSelectedFile(new java.io.File("grades_section_" + selectedSectionId + ".csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                String filePath = file.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath += ".csv";
                }
                GradeExporter.exportToCSV(filePath, response.getData());
                JOptionPane.showMessageDialog(this, "Grades exported successfully to:\n" + filePath, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to export grades: " + e.getMessage(), 
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

