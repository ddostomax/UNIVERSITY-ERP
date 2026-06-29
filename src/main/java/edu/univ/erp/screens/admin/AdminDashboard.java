package edu.univ.erp.screens.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.models.Course;
import edu.univ.erp.models.InstructorProfile;
import edu.univ.erp.models.Section;
import edu.univ.erp.screens.common.MaintenanceBanner;
import edu.univ.erp.helpers.DatabaseBackup;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JPanel {

    private final AdminApi adminApi = new AdminApi();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JTable usersTable = new JTable();
    private final JTable coursesTable = new JTable();
    private final JTable sectionsTable = new JTable();
    private final MaintenanceBanner maintenanceBanner = new MaintenanceBanner();
    private List<Section> cachedSections = new ArrayList<>();
    private Section sectionBeingEdited = null;

    public AdminDashboard() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x1E, 0x1E, 0x24));
        add(maintenanceBanner, BorderLayout.NORTH);

        tabbedPane.addTab("Users", createUsersPanel());
        tabbedPane.addTab("Courses", createCoursesPanel());
        tabbedPane.addTab("Sections", createSectionsPanel());
        tabbedPane.addTab("Settings", createSettingsPanel());

        styleTabbedPane(tabbedPane);

        add(tabbedPane, BorderLayout.CENTER);

        refreshAll();
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));
        
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(roleLabel, gbc);
        gbc.gridx = 1;
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"STUDENT", "INSTRUCTOR", "ADMIN"});
        topPanel.add(roleCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        topPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        topPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        topPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel rollNoLabel = new JLabel("Roll No:");
        rollNoLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(rollNoLabel, gbc);
        gbc.gridx = 1;
        JTextField rollNoField = new JTextField(15);
        topPanel.add(rollNoField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        JLabel programLabel = new JLabel("Program:");
        programLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(programLabel, gbc);
        gbc.gridx = 1;
        JTextField programField = new JTextField(15);
        topPanel.add(programField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(yearLabel, gbc);
        gbc.gridx = 1;
        JTextField yearField = new JTextField(15);
        topPanel.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(deptLabel, gbc);
        gbc.gridx = 1;
        JTextField deptField = new JTextField(15);
        topPanel.add(deptField, gbc);

        roleCombo.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            boolean isStudent = "STUDENT".equals(role);
            rollNoLabel.setVisible(isStudent);
            rollNoField.setVisible(isStudent);
            programLabel.setVisible(isStudent);
            programField.setVisible(isStudent);
            yearLabel.setVisible(isStudent);
            yearField.setVisible(isStudent);
            deptLabel.setVisible(!isStudent && "INSTRUCTOR".equals(role));
            deptField.setVisible(!isStudent && "INSTRUCTOR".equals(role));
        });
        roleCombo.setSelectedIndex(0);

        JButton createBtn = new JButton("Create User");
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        topPanel.add(createBtn, gbc);

        createBtn.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ApiResponse<Void> response;
            if ("STUDENT".equals(role)) {
                String rollNo = rollNoField.getText().trim();
                String program = programField.getText().trim();
                Integer year = null;
                try {
                    if (!yearField.getText().trim().isEmpty()) {
                        year = Integer.parseInt(yearField.getText().trim());
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid year.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                response = adminApi.createStudentUser(username, password, rollNo, name, program, year);
            } else if ("INSTRUCTOR".equals(role)) {
                String department = deptField.getText().trim();
                response = adminApi.createInstructorUser(username, password, name, department);
            } else {
                response = adminApi.createAdminUser(username, password);
            }

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage());
                usernameField.setText("");
                passwordField.setText("");
                nameField.setText("");
                rollNoField.setText("");
                programField.setText("");
                yearField.setText("");
                deptField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JLabel("User management - Create new users above"), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel codeLabel = new JLabel("Course Code:");
        codeLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(codeLabel, gbc);
        gbc.gridx = 1;
        JTextField codeField = new JTextField(15);
        topPanel.add(codeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(titleLabel, gbc);
        gbc.gridx = 1;
        JTextField titleField = new JTextField(15);
        topPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel creditsLabel = new JLabel("Credits:");
        creditsLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(creditsLabel, gbc);
        gbc.gridx = 1;
        JTextField creditsField = new JTextField(15);
        topPanel.add(creditsField, gbc);

        JButton createBtn = new JButton("Create Course");
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        topPanel.add(createBtn, gbc);

        createBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            String title = titleField.getText().trim();
            try {
                int credits = Integer.parseInt(creditsField.getText().trim());
                ApiResponse<Void> response = adminApi.createCourse(code, title, credits);
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    codeField.setText("");
                    titleField.setText("");
                    creditsField.setText("");
                    refreshCourses();
                } else {
                    JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid credits.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        coursesTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        buttons.add(refreshBtn);
        refreshBtn.addActionListener(e -> refreshCourses());

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSectionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(courseLabel, gbc);
        gbc.gridx = 1;
        JComboBox<Course> courseCombo = new JComboBox<>();
        topPanel.add(courseCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel instructorLabel = new JLabel("Instructor (optional):");
        instructorLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(instructorLabel, gbc);
        gbc.gridx = 1;
        JComboBox<InstructorProfile> instructorCombo = new JComboBox<>();
        instructorCombo.addItem(null);
        topPanel.add(instructorCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel dayLabel = new JLabel("Day of Week:");
        dayLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(dayLabel, gbc);
        gbc.gridx = 1;
        JTextField dayField = new JTextField(15);
        topPanel.add(dayField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel startLabel = new JLabel("Start Time (HH:mm):");
        startLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(startLabel, gbc);
        gbc.gridx = 1;
        JTextField startTimeField = new JTextField(15);
        topPanel.add(startTimeField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel endLabel = new JLabel("End Time (HH:mm):");
        endLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(endLabel, gbc);
        gbc.gridx = 1;
        JTextField endTimeField = new JTextField(15);
        topPanel.add(endTimeField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        JLabel roomLabel = new JLabel("Room:");
        roomLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(roomLabel, gbc);
        gbc.gridx = 1;
        JTextField roomField = new JTextField(15);
        topPanel.add(roomField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        JLabel capacityLabel = new JLabel("Capacity:");
        capacityLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(capacityLabel, gbc);
        gbc.gridx = 1;
        JTextField capacityField = new JTextField(15);
        topPanel.add(capacityField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(semesterLabel, gbc);
        gbc.gridx = 1;
        JTextField semesterField = new JTextField(15);
        topPanel.add(semesterField, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        JLabel yearLabel2 = new JLabel("Year:");
        yearLabel2.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(yearLabel2, gbc);
        gbc.gridx = 1;
        JTextField yearField = new JTextField(15);
        topPanel.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        JLabel dropLabel = new JLabel("Drop Deadline (YYYY-MM-DD, optional):");
        dropLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(dropLabel, gbc);
        gbc.gridx = 1;
        JTextField dropDeadlineField = new JTextField(15);
        topPanel.add(dropDeadlineField, gbc);

        Runnable resetSectionForm = () -> {
            dayField.setText("");
            startTimeField.setText("");
            endTimeField.setText("");
            roomField.setText("");
            capacityField.setText("");
            semesterField.setText("");
            yearField.setText("");
            dropDeadlineField.setText("");
            if (courseCombo.getItemCount() > 0) {
                courseCombo.setSelectedIndex(0);
            }
            if (instructorCombo.getItemCount() > 0) {
                instructorCombo.setSelectedIndex(0);
            }
            sectionBeingEdited = null;
        };

        JButton createBtn = new JButton("Create Section");
        gbc.gridx = 0; gbc.gridy = 10;
        gbc.gridwidth = 2;
        topPanel.add(createBtn, gbc);

        createBtn.addActionListener(e -> {
            Course course = (Course) courseCombo.getSelectedItem();
            if (course == null) {
                JOptionPane.showMessageDialog(this, "Please select a course.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            InstructorProfile instructor = (InstructorProfile) instructorCombo.getSelectedItem();
            String dayOfWeek = dayField.getText().trim();
            LocalTime startTime = null;
            LocalTime endTime = null;
            try {
                if (!startTimeField.getText().trim().isEmpty()) {
                    startTime = LocalTime.parse(startTimeField.getText().trim());
                }
                if (!endTimeField.getText().trim().isEmpty()) {
                    endTime = LocalTime.parse(endTimeField.getText().trim());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid time format. Use HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String room = roomField.getText().trim();
            try {
                int capacity = Integer.parseInt(capacityField.getText().trim());
                String semester = semesterField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                java.time.LocalDate dropDeadline = null;
                if (!dropDeadlineField.getText().trim().isEmpty()) {
                    try {
                        dropDeadline = java.time.LocalDate.parse(dropDeadlineField.getText().trim());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                ApiResponse<Void> response = adminApi.createSection(
                    course.getCourseId(),
                    instructor != null ? instructor.getUserId() : null,
                    dayOfWeek, startTime, endTime, room, capacity, semester, year, dropDeadline
                );
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    resetSectionForm.run();
                    refreshSections();
                } else {
                    JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        sectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionsTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(sectionsTable);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        JPanel assignPanel = new JPanel(new FlowLayout());
        assignPanel.setOpaque(false);
        assignPanel.add(new JLabel("Assign Instructor:"));
        JComboBox<InstructorProfile> assignInstructorCombo = new JComboBox<>();
        assignInstructorCombo.addItem(null);
        assignPanel.add(assignInstructorCombo);
        JButton assignBtn = new JButton("Assign");
        assignPanel.add(assignBtn);
        bottomPanel.add(assignPanel, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh");
        JButton loadBtn = new JButton("Load Selected");
        JButton updateBtn = new JButton("Update Section");
        buttons.add(refreshBtn);
        buttons.add(loadBtn);
        buttons.add(updateBtn);
        bottomPanel.add(buttons, BorderLayout.SOUTH);

        assignBtn.addActionListener(e -> {
            int row = sectionsTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a section.");
                return;
            }
            int modelRow = sectionsTable.convertRowIndexToModel(row);
            long sectionId = (Long) sectionsTable.getModel().getValueAt(modelRow, 0);
            InstructorProfile instructor = (InstructorProfile) assignInstructorCombo.getSelectedItem();
            if (instructor == null) {
                JOptionPane.showMessageDialog(this, "Please select an instructor.");
                return;
            }
            ApiResponse<Void> response = adminApi.assignInstructorToSection(sectionId, instructor.getUserId());
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage());
                refreshSections();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Helper method to populate combo boxes
        Runnable refreshComboBoxes = () -> {
            ApiResponse<List<Course>> coursesResponse = adminApi.getAllCourses();
            if (coursesResponse.isSuccess()) {
                courseCombo.removeAllItems();
                for (Course course : coursesResponse.getData()) {
                    courseCombo.addItem(course);
                }
            }
            ApiResponse<List<InstructorProfile>> instructorsResponse = adminApi.getAllInstructors();
            if (instructorsResponse.isSuccess()) {
                instructorCombo.removeAllItems();
                instructorCombo.addItem(null);
                for (InstructorProfile inst : instructorsResponse.getData()) {
                    instructorCombo.addItem(inst);
                }
                assignInstructorCombo.removeAllItems();
                assignInstructorCombo.addItem(null);
                for (InstructorProfile inst : instructorsResponse.getData()) {
                    assignInstructorCombo.addItem(inst);
                }
            }
        };

        refreshBtn.addActionListener(e -> {
            refreshCourses();
            refreshInstructors();
            refreshSections();
            refreshComboBoxes.run();
        });

        Runnable loadSelectedSection = () -> {
            int row = sectionsTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a section to edit.");
                return;
            }
            int modelRow = sectionsTable.convertRowIndexToModel(row);
            long sectionId = (Long) sectionsTable.getModel().getValueAt(modelRow, 0);
            Section match = cachedSections.stream()
                    .filter(sec -> sec.getSectionId() == sectionId)
                    .findFirst()
                    .orElse(null);
            if (match == null) {
                JOptionPane.showMessageDialog(this, "Unable to locate the selected section details.");
                return;
            }
            sectionBeingEdited = match;
            selectCourseInCombo(courseCombo, match.getCourseId());
            selectInstructorInCombo(instructorCombo, match.getInstructorId());
            dayField.setText(match.getDayOfWeek() != null ? match.getDayOfWeek() : "");
            startTimeField.setText(match.getStartTime() != null ? match.getStartTime().toString() : "");
            endTimeField.setText(match.getEndTime() != null ? match.getEndTime().toString() : "");
            roomField.setText(match.getRoom() != null ? match.getRoom() : "");
            capacityField.setText(String.valueOf(match.getCapacity()));
            semesterField.setText(match.getSemester() != null ? match.getSemester() : "");
            yearField.setText(String.valueOf(match.getYear()));
            if (match.getDropDeadline() != null) {
                dropDeadlineField.setText(match.getDropDeadline().toString());
            } else {
                dropDeadlineField.setText("");
            }
            JOptionPane.showMessageDialog(this, "Section loaded. Adjust the form fields and click 'Update Section'.");
        };

        loadBtn.addActionListener(e -> loadSelectedSection.run());
        sectionsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelectedSection.run();
                }
            }
        });

        updateBtn.addActionListener(e -> {
            if (sectionBeingEdited == null) {
                JOptionPane.showMessageDialog(this, "Load a section first before updating.");
                return;
            }
            Course course = (Course) courseCombo.getSelectedItem();
            if (course == null) {
                JOptionPane.showMessageDialog(this, "Please select a course.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            InstructorProfile instructor = (InstructorProfile) instructorCombo.getSelectedItem();
            String dayOfWeek = dayField.getText().trim();
            LocalTime startTime = null;
            LocalTime endTime = null;
            try {
                if (!startTimeField.getText().trim().isEmpty()) {
                    startTime = LocalTime.parse(startTimeField.getText().trim());
                }
                if (!endTimeField.getText().trim().isEmpty()) {
                    endTime = LocalTime.parse(endTimeField.getText().trim());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid time format. Use HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String room = roomField.getText().trim();
            try {
                int capacity = Integer.parseInt(capacityField.getText().trim());
                String semester = semesterField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                java.time.LocalDate dropDeadline = null;
                if (!dropDeadlineField.getText().trim().isEmpty()) {
                    try {
                        dropDeadline = java.time.LocalDate.parse(dropDeadlineField.getText().trim());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                ApiResponse<Void> response = adminApi.updateSection(
                        sectionBeingEdited.getSectionId(),
                        course.getCourseId(),
                        instructor != null ? instructor.getUserId() : null,
                        dayOfWeek, startTime, endTime, room, capacity, semester, year, dropDeadline
                );
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, response.getMessage());
                    resetSectionForm.run();
                    refreshSections();
                } else {
                    JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Initial population
        refreshComboBoxes.run();

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x1E, 0x1E, 0x24));
        
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Maintenance Mode
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel maintenanceLabel = new JLabel("Maintenance Mode:");
        maintenanceLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(maintenanceLabel, gbc);
        gbc.gridx = 1;
        JToggleButton toggleBtn = new JToggleButton("OFF");
        topPanel.add(toggleBtn, gbc);

        toggleBtn.addActionListener(e -> {
            boolean enabled = toggleBtn.isSelected();
            ApiResponse<Void> response = adminApi.toggleMaintenanceMode(enabled);
            if (response.isSuccess()) {
                toggleBtn.setText(enabled ? "ON" : "OFF");
                maintenanceBanner.refresh();
                JOptionPane.showMessageDialog(this, response.getMessage());
            } else {
                toggleBtn.setSelected(!enabled);
                JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Backup/Restore
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel dbLabel = new JLabel("Database:");
        dbLabel.setForeground(new Color(0xF5, 0xF5, 0xF7));
        topPanel.add(dbLabel, gbc);
        gbc.gridx = 1;
        JPanel backupPanel = new JPanel(new FlowLayout());
        backupPanel.setOpaque(false);
        JButton backupBtn = new JButton("Backup");
        JButton restoreBtn = new JButton("Restore");
        backupPanel.add(backupBtn);
        backupPanel.add(restoreBtn);
        topPanel.add(backupPanel, gbc);

        backupBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Backup As");
            chooser.setSelectedFile(new java.io.File("erp_backup_" + 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = chooser.getSelectedFile().getAbsolutePath();
                if (DatabaseBackup.backup(filePath)) {
                    JOptionPane.showMessageDialog(this, "Database backed up successfully to:\n" + filePath, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Backup failed. Make sure mysqldump is in your PATH.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        restoreBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "WARNING: This will replace all current data with the backup.\nAre you sure?", 
                "Confirm Restore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Backup File");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = chooser.getSelectedFile().getAbsolutePath();
                if (DatabaseBackup.restore(filePath)) {
                    JOptionPane.showMessageDialog(this, "Database restored successfully from:\n" + filePath, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAll();
                } else {
                    JOptionPane.showMessageDialog(this, "Restore failed. Make sure mysql is in your PATH.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        JLabel infoLabel = new JLabel("<html><center>Maintenance Mode: When ON, students and instructors can view but cannot make changes.<br/>" +
            "Backup/Restore: Requires mysqldump and mysql to be in your system PATH.</center></html>", 
            SwingConstants.CENTER);
        infoLabel.setForeground(new Color(0xA0, 0xA0, 0xB3));
        panel.add(infoLabel, BorderLayout.CENTER);
        return panel;
    }

    private void refreshAll() {
        refreshCourses();
        refreshInstructors();
        refreshSections();
        maintenanceBanner.refresh();
    }

    private void refreshCourses() {
        ApiResponse<List<Course>> response = adminApi.getAllCourses();
        if (response.isSuccess()) {
            List<Course> courses = response.getData();
            String[] columns = {"Course ID", "Code", "Title", "Credits"};
            Object[][] data = new Object[courses.size()][columns.length];
            for (int i = 0; i < courses.size(); i++) {
                Course c = courses.get(i);
                data[i][0] = c.getCourseId();
                data[i][1] = c.getCode();
                data[i][2] = c.getTitle();
                data[i][3] = c.getCredits();
            }
            coursesTable.setModel(new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            styleTableDark(coursesTable);
        }
    }

    private void updateCourseComboBox(JComboBox<Course> combo) {
        ApiResponse<List<Course>> response = adminApi.getAllCourses();
        if (response.isSuccess()) {
            combo.removeAllItems();
            for (Course course : response.getData()) {
                combo.addItem(course);
            }
        }
    }

    private void refreshInstructors() {
        ApiResponse<List<InstructorProfile>> response = adminApi.getAllInstructors();
        if (response.isSuccess()) {
            List<InstructorProfile> instructors = response.getData();
            // Find and update all instructor combo boxes in the sections panel
            Component sectionsTab = tabbedPane.getComponentAt(2);
            if (sectionsTab instanceof JPanel) {
                updateInstructorComboBoxes((JPanel) sectionsTab, instructors);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void updateInstructorComboBoxes(Container container, List<InstructorProfile> instructors) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JComboBox) {
                JComboBox<?> combo = (JComboBox<?>) comp;
                if (combo.getItemCount() > 0 && (combo.getItemAt(0) instanceof InstructorProfile || combo.getItemAt(0) == null)) {
                    combo.removeAllItems();
                    combo.addItem(null);
                    JComboBox<InstructorProfile> typedCombo = (JComboBox<InstructorProfile>) combo;
                    for (InstructorProfile inst : instructors) {
                        typedCombo.addItem(inst);
                    }
                }
            } else if (comp instanceof Container) {
                updateInstructorComboBoxes((Container) comp, instructors);
            }
        }
    }

    private void refreshSections() {
        ApiResponse<List<Section>> response = adminApi.getAllSections();
        if (response.isSuccess()) {
            List<Section> sections = response.getData();
            cachedSections = new ArrayList<>(sections);
            sectionBeingEdited = null;
            Map<Long, Course> courseDetails = buildCourseDetailMap();
            Map<Long, String> instructorNames = buildInstructorNameMap();
            String[] columns = {"Section ID", "Course Code", "Course Title", "Instructor", "Day", "Time", "Room", "Capacity", "Semester", "Year"};
            Object[][] data = new Object[sections.size()][columns.length];
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            for (int i = 0; i < sections.size(); i++) {
                Section s = sections.get(i);
                data[i][0] = s.getSectionId();
                Course course = courseDetails.get(s.getCourseId());
                data[i][1] = course != null ? course.getCode() : "Course #" + s.getCourseId();
                data[i][2] = course != null ? course.getTitle() : "";
                if (s.getInstructorId() != null) {
                    data[i][3] = instructorNames.getOrDefault(s.getInstructorId(), "User #" + s.getInstructorId());
                } else {
                    data[i][3] = "Unassigned";
                }
                data[i][4] = s.getDayOfWeek();
                if (s.getStartTime() != null && s.getEndTime() != null) {
                    data[i][5] = s.getStartTime().format(timeFormatter) + "-" + s.getEndTime().format(timeFormatter);
                } else {
                    data[i][5] = "";
                }
                data[i][6] = s.getRoom();
                data[i][7] = s.getCapacity();
                data[i][8] = s.getSemester();
                data[i][9] = s.getYear();
            }
            sectionsTable.setModel(new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
        }
    }

    private Map<Long, Course> buildCourseDetailMap() {
        Map<Long, Course> courseDetails = new HashMap<>();
        ApiResponse<List<Course>> response = adminApi.getAllCourses();
        if (response.isSuccess()) {
            for (Course course : response.getData()) {
                courseDetails.put(course.getCourseId(), course);
            }
        }
        return courseDetails;
    }

    private Map<Long, String> buildInstructorNameMap() {
        Map<Long, String> instructorNames = new HashMap<>();
        ApiResponse<List<InstructorProfile>> response = adminApi.getAllInstructors();
        if (response.isSuccess()) {
            for (InstructorProfile instructor : response.getData()) {
                instructorNames.put(instructor.getUserId(),
                        instructor.getName() + (instructor.getDepartment() != null ? " (" + instructor.getDepartment() + ")" : ""));
            }
        }
        return instructorNames;
    }

    private void selectCourseInCombo(JComboBox<Course> combo, long courseId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Course course = combo.getItemAt(i);
            if (course != null && course.getCourseId() == courseId) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectInstructorInCombo(JComboBox<InstructorProfile> combo, Long instructorId) {
        if (instructorId == null) {
            combo.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            InstructorProfile instructor = combo.getItemAt(i);
            if (instructor != null && instructor.getUserId() == instructorId) {
                combo.setSelectedIndex(i);
                return;
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

