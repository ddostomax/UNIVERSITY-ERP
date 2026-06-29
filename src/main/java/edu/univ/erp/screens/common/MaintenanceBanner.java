package edu.univ.erp.screens.common;

import edu.univ.erp.api.maintenance.MaintenanceApi;
import edu.univ.erp.api.common.ApiResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MaintenanceBanner extends JPanel {

    private final JLabel bannerLabel = new JLabel();
    private final Timer refreshTimer;

    public MaintenanceBanner() {
        setLayout(new BorderLayout());
        // Dark, subtle maintenance banner
        setBackground(new Color(40, 40, 60));
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bannerLabel.setForeground(new Color(255, 220, 150));
        bannerLabel.setFont(bannerLabel.getFont().deriveFont(Font.BOLD, 13f));
        add(bannerLabel, BorderLayout.CENTER);

        // Refresh every 5 seconds
        refreshTimer = new Timer(5000, e -> refresh());
        refreshTimer.start();
        refresh();
    }

    public void refresh() {
        MaintenanceApi api = new MaintenanceApi();
        ApiResponse<Boolean> response = api.isReadOnlyNow();
        if (response.isSuccess() && response.getData()) {
            bannerLabel.setText("⚠ MAINTENANCE MODE: Read-only access. Changes are disabled.");
            setVisible(true);
        } else {
            bannerLabel.setText("");
            setVisible(false);
        }
    }
}

