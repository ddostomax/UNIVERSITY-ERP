package edu.univ.erp.api.maintenance;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.core.SystemStatusManager;

public class MaintenanceApi {

    private final SystemStatusManager maintenanceService = new SystemStatusManager();

    public ApiResponse<Boolean> isReadOnlyNow() {
        try {
            return ApiResponse.success(maintenanceService.isLockedMode());
        } catch (Exception e) {
            return ApiResponse.failure("Failed to check maintenance mode: " + e.getMessage());
        }
    }
}

