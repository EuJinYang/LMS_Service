package com.example.lms.controller;

import com.example.lms.models.LmsModels.*;
import com.example.lms.service.LmsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LmsController.class)
class LmsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LmsService service;

    // --- Tests for GET /api/students/{studentId}/enrollments ---

    @Test
    void getStudentEnrollments_returnsEnrollments() throws Exception {
        Long studentId = 1L;
        Enrollment enrollment = new Enrollment(10L, studentId, 100L, LocalDate.now());
        when(service.getStudentEnrollments(studentId)).thenReturn(List.of(enrollment));

        mockMvc.perform(get("/api/students/{studentId}/enrollments", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].studentId").value(studentId));

        verify(service).getStudentEnrollments(studentId);
    }

    @Test
    void getStudentEnrollments_whenNoEnrollments_returnsEmptyArray() throws Exception {
        Long studentId = 2L;
        when(service.getStudentEnrollments(studentId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/students/{studentId}/enrollments", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(service).getStudentEnrollments(studentId);
    }

    @Test
    void getStudentEnrollments_whenInvalidId_returnsBadRequest() throws Exception {
        String studentId = "abc";

        mockMvc.perform(get("/api/students/{studentId}/enrollments", studentId))
                .andExpect(status().isBadRequest());

        // Service Never Called because the Request Fails Before Reaching the Controller
        verifyNoInteractions(service);
    }

    // --- Tests for GET /api/students/active ---

    @Test
    void getActiveStudents_returnsList() throws Exception {
        Student student = new Student(1L, "Jin", "jin.eu@students.plymouth.ac.uk");
        when(service.getActiveStudents()).thenReturn(List.of(student));

        mockMvc.perform(get("/api/students/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Jin"));
    }

    @Test
    void getActiveStudents_whenNoneExist_returnsEmptyArray() throws Exception {
        when(service.getActiveStudents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/students/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(service).getActiveStudents();
    }

    // --- Tests for GET /api/instructors/most-active ---

    @Test
    void getMostActiveInstructor_whenExists_returnsInstructor() throws Exception {
        Instructor instructor = new Instructor(5L, "Dr. Grace", "grace.tok@plymouth.ac.uk");
        when(service.getMostActiveInstructor()).thenReturn(Optional.of(instructor));

        mockMvc.perform(get("/api/instructors/most-active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("Dr. Grace"));
    }

    @Test
    void getMostActiveInstructor_whenNone_returnsNoContent() throws Exception {
        when(service.getMostActiveInstructor()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/instructors/most-active"))
                .andExpect(status().isNoContent());
    }

    // --- Tests for GET /api/instructors/no-enrollments ---

    @Test
    void getInstructorsWithNoEnrollments_returnsList() throws Exception {
        Instructor instructor = new Instructor(3L, "Dr. Chin", "chin@plymouth.ac.uk");
        when(service.getInstructorsWithNoEnrollments()).thenReturn(List.of(instructor));

        mockMvc.perform(get("/api/instructors/no-enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(3L));
    }

    @Test
    void getInstructorsWithNoEnrollments_whenAllHaveEnrollments_returnsEmptyArray() throws Exception {
        when(service.getInstructorsWithNoEnrollments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/instructors/no-enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());

        verify(service).getInstructorsWithNoEnrollments();
    }
}