package edu.univ.erp.screens.auth;

import edu.univ.erp.security.AuthService;
import edu.univ.erp.security.AuthService.AuthResult;
import edu.univ.erp.security.session.UserSession;
import edu.univ.erp.screens.common.MainFrame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Simple login dialog that authenticates against the Auth DB and opens the main window.
 */
public class LoginDialog extends JDialog {

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final AuthService authService = new AuthService();

    public LoginDialog(java.awt.Frame owner) {
        super(owner, "Login - University ERP", true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(400, 220);
        setLocationRelativeTo(owner);

        initUi();
    }

    private void initUi() {
        JPanel content = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("Please log in", SwingConstants.CENTER);
        content.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        form.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        form.add(passwordField, gbc);

        content.add(form, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");
        buttonsPanel.add(loginButton);
        buttonsPanel.add(cancelButton);

        loginButton.addActionListener(e -> doLogin());
        cancelButton.addActionListener(e -> System.exit(0));

        content.add(buttonsPanel, BorderLayout.SOUTH);

        setContentPane(content);
        getRootPane().setDefaultButton(loginButton);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Missing information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthResult result = authService.login(username, password);
        if (!result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(),
                    "Login failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            return;
        }

        UserSession session = result.getSession();
        // For now, just open the main frame; later we will show different dashboards by role.
        MainFrame mainFrame = new MainFrame();
        mainFrame.setTitle("University ERP - " + session.getRole() + " - " + session.getUsername());
        mainFrame.setVisible(true);

        dispose();
    }
}


