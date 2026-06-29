package edu.univ.erp.api.student;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.api.types.SectionCatalogRow;
import edu.univ.erp.api.types.StudentRegistrationRow;
import edu.univ.erp.security.session.SessionManager;
import edu.univ.erp.security.session.UserSession;
import edu.univ.erp.core.CatalogManager;
import edu.univ.erp.core.StudentActions;
import edu.univ.erp.core.StudentActions.ServiceResult;

import java.util.List;

public class StudentApi {

    private final StudentActions studentService = new StudentActions();
    private final CatalogManager catalogService = new CatalogManager();

    public ApiResponse<List<SectionCatalogRow>> viewCatalog() {
        return ApiResponse.success(catalogService.fetchCourseCatalog());
    }

    public ApiResponse<List<StudentRegistrationRow>> myRegistrations() {
        UserSession session = requireStudentSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in student session.");
        }
        try {
            return ApiResponse.success(studentService.listMyCourses(session.getUserId()));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to load registrations: " + e.getMessage());
        }
    }

    public ApiResponse<Void> register(long sectionId) {
        UserSession session = requireStudentSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in student session.");
        }
        ServiceResult result = studentService.enrollInCourse(session.getUserId(), sectionId);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<Void> drop(long sectionId) {
        UserSession session = requireStudentSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in student session.");
        }
        ServiceResult result = studentService.removeCourse(session.getUserId(), sectionId);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        } 
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<List<StudentRegistrationRow>> getTimetable() {
        UserSession session = requireStudentSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in student session.");
        }
        try {
            return ApiResponse.success(studentService.getTimetable(session.getUserId()));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to load timetable: " + e.getMessage());
        }
    }

    public ApiResponse<List<edu.univ.erp.data.GradeRepository.GradeWithCourse>> getGrades() {
        UserSession session = requireStudentSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in student session.");
        }
        try {
            return ApiResponse.success(studentService.showResults(session.getUserId()));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to load grades: " + e.getMessage());
        }
    }

    private UserSession requireStudentSession() {
        UserSession session = SessionManager.getCurrentSession();
        if (session == null || !"STUDENT".equalsIgnoreCase(session.getRole())) {
            return null;
        }
        return session;
    }
}


