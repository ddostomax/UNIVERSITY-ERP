package edu.univ.erp.core;

import edu.univ.erp.data.ConfigRepository;

// This class contains backend operations for toggling maintenance state and guarding write operations.
public class SystemStatusManager {

    private final ConfigRepository settingsDao = new ConfigRepository();

    // Determine whether the ERP should currently block student/instructor writes.
    public boolean isLockedMode() {
        try {
            String value = settingsDao.readSetting("maintenanceMode");
            return "true".equalsIgnoreCase(value);
        } catch (Exception e) {
            // Fail safe: if we cannot read the flag, assume not read-only but log in future.
            return false;
        }
    }
}


