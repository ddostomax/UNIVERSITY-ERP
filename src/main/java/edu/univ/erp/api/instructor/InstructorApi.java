package edu.univ.erp.api.instructor;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.security.session.SessionManager;
import edu.univ.erp.security.session.UserSession;
import edu.univ.erp.data.GradeRepository;
import edu.univ.erp.data.SectionRepository;
import edu.univ.erp.core.FacultyOperations;
import edu.univ.erp.core.FacultyOperations.ClassStats;
import edu.univ.erp.core.FacultyOperations.ServiceResult;

import java.util.List;
import java.util.Map;

public class InstructorApi {

    private final FacultyOperations instructorService = new FacultyOperations();

    public ApiResponse<List<SectionRepository.SectionWithCourse>> getMySections() {
        UserSession session = requireInstructorSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in instructor session.");
        }
        try {
            return ApiResponse.success(instructorService.getMySections(session.getUserId()));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to load sections: " + e.getMessage());
        }
    }

    public ApiResponse<Void> enterScore(long sectionId, long enrollmentId, String component, double score) {
        UserSession session = requireInstructorSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in instructor session.");
        }
        ServiceResult result = instructorService.updateScore(session.getUserId(), sectionId, enrollmentId, component, score);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<Void> computeFinalGrades(long sectionId, Map<String, Double> weights) {
        UserSession session = requireInstructorSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in instructor session.");
        }
        ServiceResult result = instructorService.calculateTotalGrade(session.getUserId(), sectionId, weights);
        if (result.isSuccess()) {
            return ApiResponse.success(result.getMessage(), null);
        }
        return ApiResponse.failure(result.getMessage());
    }

    public ApiResponse<ClassStats> getClassStats(long sectionId) {
        UserSession session = requireInstructorSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in instructor session.");
        }
        try {
            return ApiResponse.success(instructorService.summarizePerformance(session.getUserId(), sectionId));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to load stats: " + e.getMessage());
        }
    }

    public ApiResponse<List<GradeRepository.GradeWithStudent>> getGradesForSection(long sectionId) {
        UserSession session = requireInstructorSession();
        if (session == null) {
            return ApiResponse.failure("No logged-in instructor session.");
        }
        try {
            return ApiResponse.success(instructorService.getGradesForSection(session.getUserId(), sectionId));
        } catch (Exception e) {
            return ApiResponse.failure("Failed to load grades: " + e.getMessage());
        }
    }

    private UserSession requireInstructorSession() {
        UserSession session = SessionManager.getCurrentSession();
        if (session == null || !"INSTRUCTOR".equalsIgnoreCase(session.getRole())) {
            return null;
        }
        return session;
    }
}

