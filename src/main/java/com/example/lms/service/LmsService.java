package com.example.lms.service;

import com.example.lms.models.LmsModels.*;
import com.example.lms.repository.LmsRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LmsService {

    private final LmsRepository repository;

    public LmsService(LmsRepository repository) {
        this.repository = repository;
    }

    // F1: Retrieve All Enrollments for A Specific Student (with Full Course Details)
    public List<Enrollment> getStudentEnrollments(Long studentId) {
        // Optionally Validate Student Exists; If Not, Return Empty List or Throw Exception
        return repository.findEnrollmentsByStudentId(studentId);
    }

    // F2: List Active Students (Students Enrolled in at least One Course)
    public List<Student> getActiveStudents() {
        Set<Long> activeStudentIds = repository.findDistinctStudentIdsWithEnrollments();
        if (activeStudentIds.isEmpty()) {
            return Collections.emptyList();
        }
        // Fetch All Students & Filter by Active IDs
        return repository.findAllStudents().stream()
                .filter(s -> activeStudentIds.contains(s.getId()))
                .collect(Collectors.toList());
    }

    // F3: Identify Most Active Instructor (Highest Number of Student Enrollments)
    public Optional<Instructor> getMostActiveInstructor() {
        Map<Long, Long> enrollmentCounts = repository.countEnrollmentsPerInstructor();
        if (enrollmentCounts.isEmpty()) {
            return Optional.empty();
        }

        // Find Max Count
        Long maxCount = enrollmentCounts.values().stream()
                .max(Long::compareTo)
                .orElse(0L);

        // Get All Instructor IDs with Max Count (Handle Ties)
        List<Long> topInstructorIds = enrollmentCounts.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxCount))
                .map(Map.Entry::getKey)
                .toList();

        // For Simplicity, Return First One (but in Case of Tie any is Acceptable)
        if (topInstructorIds.isEmpty()) {
            return Optional.empty();
        }
        return repository.findInstructorById(topInstructorIds.get(0));
    }

    // F4: List Instructors with Zero Enrollments
    public List<Instructor> getInstructorsWithNoEnrollments() {
        Map<Long, Long> enrollmentCounts = repository.countEnrollmentsPerInstructor();
        return repository.findAllInstructors().stream()
                .filter(instructor -> !enrollmentCounts.containsKey(instructor.getId()) || enrollmentCounts.get(instructor.getId()) == 0)
                .collect(Collectors.toList());
    }
}