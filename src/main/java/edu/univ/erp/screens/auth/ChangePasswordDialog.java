package edu.univ.erp.screens.auth;

import edu.univ.erp.api.auth.AuthApi;
import edu.univ.erp.api.common.ApiResponse;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {

    private final JPasswordField currentPasswordField = new JPasswordField(20);
    private final JPasswordField newPasswordField = new JPasswordField(20);
    private final JPasswordField confirmPasswordField = new JPasswordField(20);
    private final AuthApi authApi = new AuthApi();

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Change Password", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        panel.add(currentPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        panel.add(newPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Confirm New Password:"), gbc);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton changeBtn = new JButton("Change Password");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(changeBtn);
        buttonPanel.add(cancelBtn);

        changeBtn.addActionListener(e -> doChangePassword());
        cancelBtn.addActionListener(e -> dispose());

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void doChangePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ApiResponse<Void> response = authApi.changePassword(currentPassword, newPassword);
        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

