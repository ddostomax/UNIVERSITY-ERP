package edu.univ.erp.core;

import edu.univ.erp.api.types.StudentRegistrationRow;
import edu.univ.erp.data.EnrollmentRepository;
import edu.univ.erp.data.GradeRepository;
import edu.univ.erp.data.SectionRepository;
import edu.univ.erp.data.StudentRepository;
import edu.univ.erp.models.Enrollment;
import edu.univ.erp.models.Grade;
import edu.univ.erp.models.Section;
import edu.univ.erp.models.StudentProfile;

import java.util.List;

// This class contains backend operations for student actions such as enrolling or viewing grades.
public class StudentActions {

    private final StudentRepository studentDao = new StudentRepository();
    private final SectionRepository sectionDao = new SectionRepository();
    private final EnrollmentRepository enrollmentDao = new EnrollmentRepository();
    private final GradeRepository gradeDao = new GradeRepository();
    private final SystemStatusManager maintenanceService = new SystemStatusManager();

    // Retrieve the list of courses that the student is currently enrolled in.
    public List<StudentRegistrationRow> listMyCourses(long studentUserId) {
        try {
            ensureStudentExists(studentUserId);
            return enrollmentDao.findRegistrationRows(studentUserId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load registrations", e);
        }
    }

    // Enroll the student into a selected course section after checking seat availability.
    public ServiceResult enrollInCourse(long studentUserId, long sectionId) {
        try {
            StudentProfile student = ensureStudentExists(studentUserId);

            if (maintenanceService.isLockedMode()) {
                return ServiceResult.failure("Maintenance mode is ON. Registrations are read-only.");
            }

            Section section = sectionDao.findById(sectionId);
            if (section == null) {
                return ServiceResult.failure("Section not found.");
            }

            Enrollment existing = enrollmentDao.lookupEnrollment(student.getUserId(), sectionId);
            if (existing != null && "ENROLLED".equalsIgnoreCase(existing.getStatus())) {
                return ServiceResult.failure("You are already registered for this section.");
            }

            int enrolledCount = enrollmentDao.countEnrolledInSection(sectionId);
            if (enrolledCount >= section.getCapacity()) {
                return ServiceResult.failure("Section full.");
            }

            enrollmentDao.recordEnrollment(student.getUserId(), sectionId, "ENROLLED");
            return ServiceResult.success("Registered successfully.");
        } catch (Exception e) {
            return ServiceResult.failure("Registration failed: " + e.getMessage());
        }
    }

    // Remove the student from a course section while honoring maintenance and deadline rules.
    public ServiceResult removeCourse(long studentUserId, long sectionId) {
        try {
            StudentProfile student = ensureStudentExists(studentUserId);

            if (maintenanceService.isLockedMode()) {
                return ServiceResult.failure("Maintenance mode is ON. Changes are blocked.");
            }

            Enrollment enrollment = enrollmentDao.lookupEnrollment(student.getUserId(), sectionId);
            if (enrollment == null || !"ENROLLED".equalsIgnoreCase(enrollment.getStatus())) {
                return ServiceResult.failure("You are not currently registered in that section.");
            }

            // Check drop deadline
            Section section = sectionDao.findById(sectionId);
            if (section != null && section.getDropDeadline() != null) {
                java.time.LocalDate today = java.time.LocalDate.now();
                if (today.isAfter(section.getDropDeadline())) {
                    return ServiceResult.failure("Drop deadline has passed. The deadline was " + section.getDropDeadline() + ".");
                }
            }

            enrollmentDao.updateStatus(enrollment.getEnrollmentId(), "DROPPED");
            return ServiceResult.success("Section dropped.");
        } catch (Exception e) {
            return ServiceResult.failure("Drop failed: " + e.getMessage());
        }
    }

    // Provide the student timetable, which mirrors the enrolled course list grouped by schedule.
    public List<StudentRegistrationRow> getTimetable(long studentUserId) {
        return listMyCourses(studentUserId);
    }

    // Show the recorded assessment results for the student across enrolled sections.
    public List<GradeRepository.GradeWithCourse> showResults(long studentUserId) {
        try {
            ensureStudentExists(studentUserId);
            return gradeDao.findByStudent(studentUserId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load grades", e);
        }
    }

    // Confirm that the student profile exists for the current session before processing actions.
    private StudentProfile ensureStudentExists(long userId) throws Exception {
        StudentProfile profile = studentDao.getStudentById(userId);
        if (profile == null) {
            throw new IllegalStateException("Student profile not found for current user.");
        }
        return profile;
    }

    public static class ServiceResult {
        private final boolean success;
        private final String message;

        private ServiceResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static ServiceResult success(String message) {
            return new ServiceResult(true, message);
        }

        public static ServiceResult failure(String message) {
            return new ServiceResult(false, message);
            }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}


