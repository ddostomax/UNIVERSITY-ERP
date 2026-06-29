package edu.univ.erp.core;

import edu.univ.erp.data.EnrollmentRepository;
import edu.univ.erp.data.GradeRepository;
import edu.univ.erp.data.FacultyRepository;
import edu.univ.erp.data.SectionRepository;
import edu.univ.erp.models.Grade;
import edu.univ.erp.models.InstructorProfile;
import edu.univ.erp.models.Section;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// This class contains backend operations for faculty tasks such as grading and section analytics.
public class FacultyOperations {

    private final FacultyRepository instructorDao = new FacultyRepository();
    private final SectionRepository sectionDao = new SectionRepository();
    private final EnrollmentRepository enrollmentDao = new EnrollmentRepository();
    private final GradeRepository gradeDao = new GradeRepository();
    private final SystemStatusManager maintenanceService = new SystemStatusManager();

    // Load only the sections assigned to the current instructor.
    public List<SectionRepository.SectionWithCourse> getMySections(long instructorUserId) {
        try {
            ensureInstructorExists(instructorUserId);
            return sectionDao.findByInstructor(instructorUserId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sections", e);
        }
    }

    // Update an individual assessment score for a specific enrollment.
    public ServiceResult updateScore(long instructorUserId, long sectionId, long enrollmentId, 
                                     String component, double score) {
        try {
            ensureInstructorExists(instructorUserId);
            
            if (maintenanceService.isLockedMode()) {
                return ServiceResult.failure("Maintenance mode is ON. Changes are blocked.");
            }

            Section section = sectionDao.findById(sectionId);
            if (section == null) {
                return ServiceResult.failure("Section not found.");
            }
            if (section.getInstructorId() == null || section.getInstructorId() != instructorUserId) {
                return ServiceResult.failure("Not your section.");
            }

            if (score < 0 || score > 100) {
                return ServiceResult.failure("Score must be between 0 and 100.");
            }

            gradeDao.insertOrUpdateGrade(enrollmentId, component, score);
            return ServiceResult.success("Score saved.");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to save score: " + e.getMessage());
        }
    }

    // Calculate the weighted final grade for a section using the provided weight map.
    public ServiceResult calculateTotalGrade(long instructorUserId, long sectionId, 
                                            Map<String, Double> weights) {
        try {
            ensureInstructorExists(instructorUserId);
            
            if (maintenanceService.isLockedMode()) {
                return ServiceResult.failure("Maintenance mode is ON. Changes are blocked.");
            }

            Section section = sectionDao.findById(sectionId);
            if (section == null) {
                return ServiceResult.failure("Section not found.");
            }
            if (section.getInstructorId() == null || section.getInstructorId() != instructorUserId) {
                return ServiceResult.failure("Not your section.");
            }

            double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
            if (Math.abs(totalWeight - 100.0) > 0.01) {
                return ServiceResult.failure("Weights must sum to 100.");
            }

            List<EnrollmentRepository.EnrollmentWithStudent> enrollments = enrollmentDao.findBySection(sectionId);
            
            int computed = 0;
            for (EnrollmentRepository.EnrollmentWithStudent ews : enrollments) {
                long enrollmentId = ews.enrollment.getEnrollmentId();
                List<Grade> grades = gradeDao.findByEnrollment(enrollmentId);
                
                double weightedSum = 0.0;
                boolean hasAllComponents = true;
                
                for (Map.Entry<String, Double> entry : weights.entrySet()) {
                    String component = entry.getKey();
                    Double weight = entry.getValue();
                    
                    Grade grade = grades.stream()
                            .filter(g -> component.equalsIgnoreCase(g.getComponent()))
                            .findFirst()
                            .orElse(null);
                    
                    if (grade == null) {
                        hasAllComponents = false;
                        break;
                    }
                    
                    weightedSum += grade.getScore() * weight / 100.0;
                }
                
                if (hasAllComponents) {
                    String finalGrade = calculateLetterGrade(weightedSum);
                    gradeDao.updateFinalGrade(enrollmentId, finalGrade);
                    computed++;
                }
            }
            
            return ServiceResult.success("Computed final grades for " + computed + " students.");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to compute final grades: " + e.getMessage());
        }
    }

    // Summarize the class performance metrics for instructor dashboards.
    public ClassStats summarizePerformance(long instructorUserId, long sectionId) {
        try {
            ensureInstructorExists(instructorUserId);
            
            Section section = sectionDao.findById(sectionId);
            if (section == null) {
                throw new RuntimeException("Section not found.");
            }
            if (section.getInstructorId() == null || section.getInstructorId() != instructorUserId) {
                throw new RuntimeException("Not your section.");
            }

            List<GradeRepository.GradeWithStudent> allGrades = gradeDao.findBySection(sectionId);
            
            Map<String, List<Double>> scoresByComponent = allGrades.stream()
                    .collect(Collectors.groupingBy(
                            gws -> gws.grade.getComponent(),
                            Collectors.mapping(gws -> gws.grade.getScore(), Collectors.toList())
                    ));
            
            ClassStats stats = new ClassStats();
            stats.totalStudents = enrollmentDao.findBySection(sectionId).size();
            
            for (Map.Entry<String, List<Double>> entry : scoresByComponent.entrySet()) {
                String component = entry.getKey();
                List<Double> scores = entry.getValue();
                
                if (!scores.isEmpty()) {
                    double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    double min = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                    double max = scores.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                    
                    stats.componentAverages.put(component, avg);
                    stats.componentMins.put(component, min);
                    stats.componentMaxs.put(component, max);
                }
            }
            
            return stats;
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute stats", e);
        }
    }

    // Retrieve every grade entry tied to a section for export or review.
    public List<GradeRepository.GradeWithStudent> getGradesForSection(long instructorUserId, long sectionId) {
        try {
            ensureInstructorExists(instructorUserId);
            
            Section section = sectionDao.findById(sectionId);
            if (section == null) {
                throw new RuntimeException("Section not found.");
            }
            if (section.getInstructorId() == null || section.getInstructorId() != instructorUserId) {
                throw new RuntimeException("Not your section.");
            }
            
            return gradeDao.findBySection(sectionId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load grades", e);
        }
    }

    private String calculateLetterGrade(double percentage) {
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 60) return "D";
        return "F";
    }

    private InstructorProfile ensureInstructorExists(long userId) throws Exception {
        InstructorProfile profile = instructorDao.findByUserId(userId);
        if (profile == null) {
            throw new IllegalStateException("Instructor profile not found for current user.");
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

    public static class ClassStats {
        public int totalStudents;
        public Map<String, Double> componentAverages = new java.util.HashMap<>();
        public Map<String, Double> componentMins = new java.util.HashMap<>();
        public Map<String, Double> componentMaxs = new java.util.HashMap<>();
    }
}

