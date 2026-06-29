package edu.univ.erp.core;

import edu.univ.erp.security.hash.PasswordHasher;
import edu.univ.erp.security.store.UserAuthDao;
import edu.univ.erp.data.*;
import edu.univ.erp.models.Course;
import edu.univ.erp.models.InstructorProfile;
import edu.univ.erp.models.Section;
import edu.univ.erp.models.StudentProfile;

import java.time.LocalTime;
import java.util.List;

// This class contains backend operations for administrative workflows such as user onboarding and course setup.
public class AdminOperations {

    private final UserAuthDao userAuthDao = new UserAuthDao();
    private final StudentRepository studentDao = new StudentRepository();
    private final FacultyRepository instructorDao = new FacultyRepository();
    private final CourseRepository courseDao = new CourseRepository();
    private final SectionRepository sectionDao = new SectionRepository();
    private final ConfigRepository settingsDao = new ConfigRepository();

    // Create both auth and ERP records for a new student.
    public ServiceResult createStudentUser(String username, String password, String rollNo, 
                                          String name, String program, Integer year) {
        try {
            // Check if username exists
            if (userAuthDao.findByUsername(username) != null) {
                return ServiceResult.failure("Username already exists.");
            }

            // Create user in Auth DB
            String passwordHash = PasswordHasher.hash(password);
            long userId = userAuthDao.createUser(username, "STUDENT", passwordHash);

            // Create student profile in ERP DB
            studentDao.insertStudent(userId, rollNo, name, program, year);

            return ServiceResult.success("Student user created successfully.");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to create student: " + e.getMessage());
        }
    }

    // Create both auth and ERP records for a new instructor.
    public ServiceResult createInstructorUser(String username, String password, String name, String department) {
        try {
            // Check if username exists
            if (userAuthDao.findByUsername(username) != null) {
                return ServiceResult.failure("Username already exists.");
            }

            // Create user in Auth DB
            String passwordHash = PasswordHasher.hash(password);
            long userId = userAuthDao.createUser(username, "INSTRUCTOR", passwordHash);

            // Create instructor profile in ERP DB
            instructorDao.createInstructor(userId, name, department);

            return ServiceResult.success("Instructor user created successfully.");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to create instructor: " + e.getMessage());
        }
    }

    // Create a new administrator account in the auth database.
    public ServiceResult createAdminUser(String username, String password) {
        try {
            // Check if username exists
            if (userAuthDao.findByUsername(username) != null) {
                return ServiceResult.failure("Username already exists.");
            }

            // Create user in Auth DB
            String passwordHash = PasswordHasher.hash(password);
            userAuthDao.createUser(username, "ADMIN", passwordHash);

            return ServiceResult.success("Admin user created successfully.");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to create admin: " + e.getMessage());
        }
    }

    // Define a new course entry that can later have sections allocated.
    public ServiceResult defineNewCourse(String code, String title, int credits) {
        try {
            if (code == null || code.trim().isEmpty()) {
                return ServiceResult.failure("Course code is required.");
            }
            if (title == null || title.trim().isEmpty()) {
                return ServiceResult.failure("Course title is required.");
            }
            if (credits <= 0) {
                return ServiceResult.failure("Credits must be positive.");
            }

            courseDao.createCourse(code, title, credits);
            return ServiceResult.success("Course created successfully.");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to create course: " + e.getMessage());
        }
    }

    // Allocate a section for a course and optionally tie it to an instructor and schedule.
    public ServiceResult allocateSection(long courseId, Long instructorId, String dayOfWeek,
                                      LocalTime startTime, LocalTime endTime, String room,
                                      int capacity, String semester, int year, 
                                      java.time.LocalDate dropDeadline) {
        try {
            // Validate course exists
            Course course = courseDao.findById(courseId);
            if (course == null) {
                return ServiceResult.failure("Course not found.");
            }

            if (capacity <= 0) {
                return ServiceResult.failure("Capacity must be positive.");
            }

            if (instructorId != null) {
                InstructorProfile instructor = instructorDao.findByUserId(instructorId);
                if (instructor == null) {
                    return ServiceResult.failure("Instructor not found.");
                }
            }

            sectionDao.createSection(courseId, instructorId, dayOfWeek, startTime, endTime, 
                                   room, capacity, semester, year, dropDeadline);
            return ServiceResult.success("Section created successfully.");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to create section: " + e.getMessage());
        }
    }

    // Update an existing section's schedule, course mapping, and limits.
    public ServiceResult updateSectionDetails(long sectionId, long courseId, Long instructorId, String dayOfWeek,
                                              LocalTime startTime, LocalTime endTime, String room,
                                              int capacity, String semester, int year,
                                              java.time.LocalDate dropDeadline) {
        try {
            Section existing = sectionDao.findById(sectionId);
            if (existing == null) {
                return ServiceResult.failure("Section not found.");
            }
            Course course = courseDao.findById(courseId);
            if (course == null) {
                return ServiceResult.failure("Course not found.");
            }

            if (capacity <= 0) {
                return ServiceResult.failure("Capacity must be positive.");
            }

            if (instructorId != null) {
                InstructorProfile instructor = instructorDao.findByUserId(instructorId);
                if (instructor == null) {
                    return ServiceResult.failure("Instructor not found.");
                }
            }

            sectionDao.updateSection(sectionId, courseId, instructorId, dayOfWeek, startTime, endTime,
                    room, capacity, semester, year, dropDeadline);
            return ServiceResult.success("Section updated successfully.");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to update section: " + e.getMessage());
        }
    }

    // Map an instructor to an existing course section.
    public ServiceResult mapInstructorToSection(long sectionId, long instructorId) {
        try {
            Section section = sectionDao.findById(sectionId);
            if (section == null) {
                return ServiceResult.failure("Section not found.");
            }

            InstructorProfile instructor = instructorDao.findByUserId(instructorId);
            if (instructor == null) {
                return ServiceResult.failure("Instructor not found.");
            }

            sectionDao.assignInstructor(sectionId, instructorId);
            return ServiceResult.success("Instructor assigned successfully.");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to assign instructor: " + e.getMessage());
        }
    }

    // Switch the maintenance flag to lock or unlock instructor/student actions.
    public ServiceResult switchSystemMode(boolean enabled) {
        try {
            settingsDao.changeSetting("maintenanceMode", enabled ? "true" : "false");
            return ServiceResult.success("Maintenance mode " + (enabled ? "enabled" : "disabled") + ".");
        } catch (Exception e) {
            return ServiceResult.failure("Failed to toggle maintenance mode: " + e.getMessage());
        }
    }

    // Retrieve every course definition for admin screens.
    public List<Course> getAllCourses() {
        try {
            return courseDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load courses", e);
        }
    }

    // Retrieve every section regardless of instructor assignment.
    public List<Section> getAllSections() {
        try {
            return sectionDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sections", e);
        }
    }

    // Retrieve every instructor profile for dropdown selections.
    public List<InstructorProfile> getAllInstructors() {
        try {
            return instructorDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load instructors", e);
        }
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

