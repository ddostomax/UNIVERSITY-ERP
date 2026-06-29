package edu.univ.erp.api.catalog;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.api.types.SectionCatalogRow;
import edu.univ.erp.core.CatalogManager;

import java.util.List;

public class CatalogApi {

    private final CatalogManager catalogService = new CatalogManager();

    public ApiResponse<List<SectionCatalogRow>> listCatalog() {
        try {
            return ApiResponse.success(catalogService.fetchCourseCatalog());
        } catch (Exception e) {
            return ApiResponse.failure("Failed to load catalog: " + e.getMessage());
        }
    }
}


