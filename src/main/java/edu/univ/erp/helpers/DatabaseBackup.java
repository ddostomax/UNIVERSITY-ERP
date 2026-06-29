package edu.univ.erp.helpers;

import edu.univ.erp.data.DBConnector;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Utility class for backing up and restoring the ERP database.
 */
public class DatabaseBackup {

    /**
     * Backup the ERP database to a SQL file.
     */
    public static boolean backup(String filePath) {
        try {
            Properties props = new Properties();
            props.load(DatabaseBackup.class.getClassLoader().getResourceAsStream("db.properties"));
            
            String dbUrl = props.getProperty("erp.jdbc.url");
            String dbUser = props.getProperty("erp.jdbc.user");
            String dbPassword = props.getProperty("erp.jdbc.password");
            
            // Extract database name from URL
            String dbName = "univ_erp";
            
            // Build mysqldump command
            String command = String.format(
                "mysqldump -u%s -p%s %s > \"%s\"",
                dbUser, dbPassword, dbName, filePath
            );
            
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                return true;
            } else {
                // Try alternative approach for Windows
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    return backupWindows(dbUser, dbPassword, dbName, filePath);
                }
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Backup failed: " + e.getMessage() + "\n\nNote: mysqldump must be in your PATH.", 
                "Backup Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static boolean backupWindows(String user, String password, String dbName, String filePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe", "/c", 
                String.format("mysqldump -u%s -p%s %s > \"%s\"", user, password, dbName, filePath)
            );
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Restore the ERP database from a SQL file.
     */
    public static boolean restore(String filePath) {
        try {
            Properties props = new Properties();
            props.load(DatabaseBackup.class.getClassLoader().getResourceAsStream("db.properties"));
            
            String dbUrl = props.getProperty("erp.jdbc.url");
            String dbUser = props.getProperty("erp.jdbc.user");
            String dbPassword = props.getProperty("erp.jdbc.password");
            
            String dbName = "univ_erp";
            
            // Build mysql command
            String command = String.format(
                "mysql -u%s -p%s %s < \"%s\"",
                dbUser, dbPassword, dbName, filePath
            );
            
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                return true;
            } else {
                // Try alternative approach for Windows
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    return restoreWindows(dbUser, dbPassword, dbName, filePath);
                }
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Restore failed: " + e.getMessage() + "\n\nNote: mysql must be in your PATH.", 
                "Restore Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static boolean restoreWindows(String user, String password, String dbName, String filePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe", "/c", 
                String.format("mysql -u%s -p%s %s < \"%s\"", user, password, dbName, filePath)
            );
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
}

