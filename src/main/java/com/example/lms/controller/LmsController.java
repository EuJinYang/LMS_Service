package com.example.lms.controller;

import com.example.lms.models.LmsModels.*;
import com.example.lms.service.LmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LmsController {

    private final LmsService service;

    public LmsController(LmsService service) {
        this.service = service;
    }

    // F1: Get Enrollments for A Student
    @GetMapping("/students/{studentId}/enrollments")
    public ResponseEntity<List<Enrollment>> getStudentEnrollments(@PathVariable Long studentId) {
        List<Enrollment> enrollments = service.getStudentEnrollments(studentId);
        return ResponseEntity.ok(enrollments);
    }

    // F2: List Active Students
    @GetMapping("/students/active")
    public ResponseEntity<List<Student>> getActiveStudents() {
        List<Student> activeStudents = service.getActiveStudents();
        return ResponseEntity.ok(activeStudents);
    }

    // F3: Most Active Instructor
    @GetMapping("/instructors/most-active")
    public ResponseEntity<Instructor> getMostActiveInstructor() {
        return service.getMostActiveInstructor()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // F4: Instructors with No Enrollments
    @GetMapping("/instructors/no-enrollments")
    public ResponseEntity<List<Instructor>> getInstructorsWithNoEnrollments() {
        List<Instructor> instructors = service.getInstructorsWithNoEnrollments();
        return ResponseEntity.ok(instructors);
    }
}