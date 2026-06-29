package edu.univ.erp;

import com.formdev.flatlaf.FlatDarkLaf;
import edu.univ.erp.screens.auth.LoginDialog;

import javax.swing.SwingUtilities;

/**
 * Entry point for the University ERP desktop application.
 */
public class App {

    public static void main(String[] args) {
        // Use a modern dark look & feel across the app
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);
        });
    }
}



