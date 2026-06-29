package edu.univ.erp.screens.common;

import edu.univ.erp.security.AuthService;
import edu.univ.erp.security.session.SessionManager;
import edu.univ.erp.security.session.UserSession;
import edu.univ.erp.screens.admin.AdminDashboard;
import edu.univ.erp.screens.auth.ChangePasswordDialog;
import edu.univ.erp.screens.auth.LoginDialog;
import edu.univ.erp.screens.instructor.InstructorDashboard;
import edu.univ.erp.screens.student.StudentDashboard;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame that shows role-specific dashboards.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("University ERP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Top menu bar with account actions
        JMenuBar menuBar = new JMenuBar();
        JMenu accountMenu = new JMenu("Account");

        JMenuItem changePasswordItem = new JMenuItem("Change Password");
        changePasswordItem.addActionListener(e -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(this);
            dialog.setVisible(true);
        });
        accountMenu.add(changePasswordItem);

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            new AuthService().logout();
            dispose();
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);
        });
        accountMenu.add(logoutItem);

        menuBar.add(accountMenu);
        setJMenuBar(menuBar);

        UserSession session = SessionManager.getCurrentSession();
        if (session == null) {
            setContentPane(new JLabel("No session found", SwingConstants.CENTER));
            return;
        }

        String role = session.getRole();

        if ("STUDENT".equalsIgnoreCase(role)) {
            setContentPane(new StudentDashboard());
        } else if ("INSTRUCTOR".equalsIgnoreCase(role)) {
            setContentPane(new InstructorDashboard());
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            setContentPane(new AdminDashboard());
        } else {
            setContentPane(new JLabel("Unknown role: " + role, SwingConstants.CENTER));
        }
    }
}
