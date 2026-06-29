package edu.univ.erp.api.admin;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.security.session.SessionManager;
import edu.univ.erp.security.session.UserSession;
import edu.univ.erp.models.Course;
import edu.univ.erp.models.InstructorProfile;
import edu.univ.erp.models.Section;
import edu.univ.erp.core.AdminOperations;
import edu.univ.erp.core.AdminOperations.ServiceResult;

import java.time.LocalTime;
import java.util.List;

public class AdminApi {

    private final AdminOperations adminService = new AdminOperations();

    public ApiResponse<Void> createStudentUser(String username, String password, String rollNo,
                                              String name, String program, Integer year) {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        ServiceResult result = adminService.createStudentUser(username, password, rollNo, name, program, year);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<Void> createInstructorUser(String username, String password, String name, String department) {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        ServiceResult result = adminService.createInstructorUser(username, password, name, department);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<Void> createAdminUser(String username, String password) {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        ServiceResult result = adminService.createAdminUser(username, password);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<Void> createCourse(String code, String title, int credits) {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        ServiceResult result = adminService.defineNewCourse(code, title, credits);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<Void> createSection(long courseId, Long instructorId, String dayOfWeek,
                                          LocalTime startTime, LocalTime endTime, String room,
                                          int capacity, String semester, int year, 
                                          java.time.LocalDate dropDeadline) {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        ServiceResult result = adminService.allocateSection(courseId, instructorId, dayOfWeek, 
                                                         startTime, endTime, room, capacity, semester, year, dropDeadline);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<Void> updateSection(long sectionId, long courseId, Long instructorId, String dayOfWeek,
                                           LocalTime startTime, LocalTime endTime, String room,
                                           int capacity, String semester, int year,
                                           java.time.LocalDate dropDeadline) {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        ServiceResult result = adminService.updateSectionDetails(sectionId, courseId, instructorId,
                dayOfWeek, startTime, endTime, room, capacity, semester, year, dropDeadline);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<Void> assignInstructorToSection(long sectionId, long instructorId) {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        ServiceResult result = adminService.mapInstructorToSection(sectionId, instructorId);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<Void> toggleMaintenanceMode(boolean enabled) {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        ServiceResult result = adminService.switchSystemMode(enabled);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<List<Course>> getAllCourses() {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        try {
            return ApiResponse.success(adminService.getAllCourses());
        } catch (Exception e) {
            return ApiResponse.failure("Failed to load courses: " + e.getMessage());
        }
    }

    public ApiResponse<List<Section>> getAllSections() {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        try {
            return ApiResponse.success(adminService.getAllSections());
        } catch (Exception e) {
            return ApiResponse.failure("Failed to load sections: " + e.getMessage());
        }
    }

    public ApiResponse<List<InstructorProfile>> getAllInstructors() {
        UserSession session = requireAdminSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in admin session.");
        }
        try {
            return ApiResponse.success(adminService.getAllInstructors());
        } catch (Exception e) {
            return ApiResponse.failure("Failed to load instructors: " + e.getMessage());
        }
    }

    private UserSession requireAdminSession() {
        UserSession session = SessionManager.getCurrentSession();
        if (session == null || !"ADMIN".equalsIgnoreCase(session.getRole())) {
            return null;
        }
        return session;
    }
}

