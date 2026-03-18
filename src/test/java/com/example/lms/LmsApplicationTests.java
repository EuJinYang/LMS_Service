package com.example.lms;

import com.example.lms.models.LmsModels.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LmsApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Test
    void testF1_getStudentEnrollments() {
        // Student 1 should have 2 Enrollments (from Sample Data)
        ResponseEntity<List<Enrollment>> response = restTemplate.exchange(
                baseUrl() + "/students/1/enrollments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Enrollment>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testF1_getStudentEnrollments_noEnrollments() {
        // Student 4 has no Enrollments (from Sample Data)
        ResponseEntity<List<Enrollment>> response = restTemplate.exchange(
                baseUrl() + "/students/4/enrollments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Enrollment>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void testF2_getActiveStudents() {
        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl() + "/students/active",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Students 1,2,3 are Active (student4 has No Enrollments)
        assertThat(response.getBody()).hasSize(3);
    }

    @Test
    void testF3_getMostActiveInstructor() {
        ResponseEntity<Instructor> response = restTemplate.getForEntity(
                baseUrl() + "/instructors/most-active", Instructor.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Instructor 1 (Dr. Grace) has 2 Enrollments (from Courses 1 & 2), Others have Fewer
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void testF4_getInstructorsWithNoEnrollments() {
        ResponseEntity<List<Instructor>> response = restTemplate.exchange(
                baseUrl() + "/instructors/no-enrollments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Instructor>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Instructor 3 (Dr. Chin) has no Enrollments (Course 4 has no Students)
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(3L);
    }
}