package com.example.lms.repository;

import com.example.lms.models.LmsModels.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LmsRepository {

    // In-Memory Storage
    private final Map<Long, Student> students = new ConcurrentHashMap<>();
    private final Map<Long, Instructor> instructors = new ConcurrentHashMap<>();
    private final Map<Long, Course> courses = new ConcurrentHashMap<>();
    private final Map<Long, Enrollment> enrollments = new ConcurrentHashMap<>();

    // ID Generators
    private final AtomicLong studentIdGen = new AtomicLong(1);
    private final AtomicLong instructorIdGen = new AtomicLong(1);
    private final AtomicLong courseIdGen = new AtomicLong(1);
    private final AtomicLong enrollmentIdGen = new AtomicLong(1);

    // Initialize with Sample Data
    public LmsRepository() {
        initSampleData();
    }

    private void initSampleData() {
        // Instructors
        Instructor inst1 = new Instructor(instructorIdGen.getAndIncrement(), "Dr. Grace", "grace.tok@plymouth.ac.uk");
        Instructor inst2 = new Instructor(instructorIdGen.getAndIncrement(), "Dr. Eric", "eric.kong@plymouth.ac.uk");
        Instructor inst3 = new Instructor(instructorIdGen.getAndIncrement(), "Dr. Chin", "chin@plymouth.ac.uk");
        instructors.put(inst1.getId(), inst1);
        instructors.put(inst2.getId(), inst2);
        instructors.put(inst3.getId(), inst3);

        // Courses
        Course course1 = new Course(courseIdGen.getAndIncrement(), "Artificial Intelligence", inst1.getId());
        Course course2 = new Course(courseIdGen.getAndIncrement(), "Software Engineering", inst1.getId());
        Course course3 = new Course(courseIdGen.getAndIncrement(), "Computing Practice", inst2.getId());
        Course course4 = new Course(courseIdGen.getAndIncrement(), "Big Data Analytics", inst3.getId());
        courses.put(course1.getId(), course1);
        courses.put(course2.getId(), course2);
        courses.put(course3.getId(), course3);
        courses.put(course4.getId(), course4);

        // Students
        Student student1 = new Student(studentIdGen.getAndIncrement(), "Jin", "jin.eu@students.plymouth.ac.uk");
        Student student2 = new Student(studentIdGen.getAndIncrement(), "Terence", "terence.lim@students.plymouth.ac.uk");
        Student student3 = new Student(studentIdGen.getAndIncrement(), "Kean Chun", "kean.ng@students.plymouth.ac.uk");
        Student student4 = new Student(studentIdGen.getAndIncrement(), "Yong Ken", "ken@students.plymouth.ac.uk");
        students.put(student1.getId(), student1);
        students.put(student2.getId(), student2);
        students.put(student3.getId(), student3);
        students.put(student4.getId(), student4);

        // Enrollments (Student -> Course)
        enrollments.put(enrollmentIdGen.getAndIncrement(),
                new Enrollment(enrollmentIdGen.get(), student1.getId(), course1.getId(), LocalDate.now().minusDays(10)));
        enrollments.put(enrollmentIdGen.getAndIncrement(),
                new Enrollment(enrollmentIdGen.get(), student1.getId(), course2.getId(), LocalDate.now().minusDays(5)));
        enrollments.put(enrollmentIdGen.getAndIncrement(),
                new Enrollment(enrollmentIdGen.get(), student2.getId(), course1.getId(), LocalDate.now().minusDays(8)));
        enrollments.put(enrollmentIdGen.getAndIncrement(),
                new Enrollment(enrollmentIdGen.get(), student3.getId(), course3.getId(), LocalDate.now().minusDays(12)));
        // student4 has No Enrollments
    }

    // --- Student Operations ---
    public Optional<Student> findStudentById(Long id) {
        return Optional.ofNullable(students.get(id));
    }

    public List<Student> findAllStudents() {
        return new ArrayList<>(students.values());
    }

    // --- Instructor Operations ---
    public Optional<Instructor> findInstructorById(Long id) {
        return Optional.ofNullable(instructors.get(id));
    }

    public List<Instructor> findAllInstructors() {
        return new ArrayList<>(instructors.values());
    }

    // --- Course Operations ---
    public Optional<Course> findCourseById(Long id) {
        return Optional.ofNullable(courses.get(id));
    }

    public List<Course> findAllCourses() {
        return new ArrayList<>(courses.values());
    }

    // --- Enrollment Operations ---
    public List<Enrollment> findAllEnrollments() {
        return new ArrayList<>(enrollments.values());
    }

    public List<Enrollment> findEnrollmentsByStudentId(Long studentId) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : enrollments.values()) {
            if (e.getStudentId().equals(studentId)) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Enrollment> findEnrollmentsByCourseId(Long courseId) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : enrollments.values()) {
            if (e.getCourseId().equals(courseId)) {
                result.add(e);
            }
        }
        return result;
    }

    // Additional Helper Methods for Service
    public Set<Long> findDistinctStudentIdsWithEnrollments() {
        Set<Long> studentIds = new HashSet<>();
        for (Enrollment e : enrollments.values()) {
            studentIds.add(e.getStudentId());
        }
        return studentIds;
    }

    public Map<Long, Long> countEnrollmentsPerInstructor() {
        Map<Long, Long> counts = new HashMap<>();
        for (Enrollment e : enrollments.values()) {
            Course c = courses.get(e.getCourseId());
            if (c != null) {
                Long instructorId = c.getInstructorId();
                counts.put(instructorId, counts.getOrDefault(instructorId, 0L) + 1);
            }
        }
        return counts;
    }
}