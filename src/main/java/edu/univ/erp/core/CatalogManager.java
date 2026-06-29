package edu.univ.erp.core;

import edu.univ.erp.api.types.SectionCatalogRow;
import edu.univ.erp.data.CatalogRepository;

import java.util.List;

// This class contains backend operations for catalog visibility such as fetching available sections.
public class CatalogManager {

    private final CatalogRepository catalogDao = new CatalogRepository();

    // Fetch the aggregated course catalog rows for students and admins.
    public List<SectionCatalogRow> fetchCourseCatalog() {
        try {
            return catalogDao.listCatalog();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load catalog", e);
        }
    }
}


