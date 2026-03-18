package com.example.lms.service;

import com.example.lms.models.LmsModels.*;
import com.example.lms.repository.LmsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LmsServiceTest {

    @Mock
    private LmsRepository repository;

    @InjectMocks
    private LmsService service;

    private Student student1, student2, student3;
    private Instructor instructor1, instructor2, instructor3;
    private Enrollment enrollment1, enrollment2, enrollment3, enrollment4;

    @BeforeEach
    void setUp() {
        student1 = new Student(1L, "Jin", "jin.eu@students.plymouth.ac.uk");
        student2 = new Student(2L, "Terence", "terence.lim@students.plymouth.ac.uk");
        student3 = new Student(3L, "Kean Chun", "kean.ng@students.plymouth.ac.uk");

        instructor1 = new Instructor(1L, "Dr. Grace", "grace.tok@plymouth.ac.uk");
        instructor2 = new Instructor(2L, "Dr. Eric", "eric.kong@plymouth.ac.uk");
        instructor3 = new Instructor(3L, "Dr. Chin", "chin@plymouth.ac.uk");

        Course course1 = new Course(1L, "Artificial Intelligence", instructor1.getId());
        Course course2 = new Course(2L, "Software Engineering", instructor1.getId());
        Course course3 = new Course(3L, "Computing Practice", instructor2.getId());

        enrollment1 = new Enrollment(1L, student1.getId(), course1.getId(), LocalDate.now());
        enrollment2 = new Enrollment(2L, student1.getId(), course2.getId(), LocalDate.now());
        enrollment3 = new Enrollment(3L, student2.getId(), course1.getId(), LocalDate.now());
        enrollment4 = new Enrollment(4L, student3.getId(), course3.getId(), LocalDate.now());
    }

    // --- Tests for getActiveStudents ---

    @Test
    void getActiveStudents_whenNoEnrollments_returnsEmptyList() {
        when(repository.findDistinctStudentIdsWithEnrollments()).thenReturn(Collections.emptySet());
        List<Student> result = service.getActiveStudents();
        assertThat(result).isEmpty();
        verify(repository, never()).findAllStudents();
    }

    @Test
    void getActiveStudents_withOneActiveStudent_returnsThatStudent() {
        Set<Long> activeIds = Set.of(student1.getId());
        when(repository.findDistinctStudentIdsWithEnrollments()).thenReturn(activeIds);
        when(repository.findAllStudents()).thenReturn(List.of(student1, student2, student3));

        List<Student> result = service.getActiveStudents();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(student1.getId());
    }

    @Test
    void getActiveStudents_withMultipleActiveStudents_returnsAllActive() {
        Set<Long> activeIds = Set.of(student1.getId(), student2.getId());
        when(repository.findDistinctStudentIdsWithEnrollments()).thenReturn(activeIds);
        when(repository.findAllStudents()).thenReturn(List.of(student1, student2, student3));

        List<Student> result = service.getActiveStudents();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Student::getId).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void getActiveStudents_whenStudentIdNotFound_returnsEmptyList() {
        when(repository.findDistinctStudentIdsWithEnrollments()).thenReturn(Set.of(99L));
        when(repository.findAllStudents()).thenReturn(Collections.emptyList());

        List<Student> result = service.getActiveStudents();

        assertThat(result).isEmpty();
    }

    // --- Tests for getMostActiveInstructor ---

    @Test
    void getMostActiveInstructor_whenNoEnrollments_returnsEmptyOptional() {
        when(repository.countEnrollmentsPerInstructor()).thenReturn(Collections.emptyMap());
        Optional<Instructor> result = service.getMostActiveInstructor();
        assertThat(result).isEmpty();
    }

    @Test
    void getMostActiveInstructor_withClearWinner_returnsThatInstructor() {
        Map<Long, Long> counts = new HashMap<>();
        counts.put(instructor1.getId(), 3L);
        counts.put(instructor2.getId(), 1L);
        counts.put(instructor3.getId(), 0L);
        when(repository.countEnrollmentsPerInstructor()).thenReturn(counts);
        when(repository.findInstructorById(instructor1.getId())).thenReturn(Optional.of(instructor1));

        Optional<Instructor> result = service.getMostActiveInstructor();

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(instructor1.getId());
    }

    @Test
    void getMostActiveInstructor_withTie_returnsAnyOneOfTheTied() {
        Map<Long, Long> counts = new HashMap<>();
        counts.put(instructor1.getId(), 2L);
        counts.put(instructor2.getId(), 2L);
        counts.put(instructor3.getId(), 1L);
        when(repository.countEnrollmentsPerInstructor()).thenReturn(counts);

        // Single Stub that Returns the Appropriate Instructor for Any ID
        when(repository.findInstructorById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            if (id.equals(instructor1.getId())) {
                return Optional.of(instructor1);
            } else if (id.equals(instructor2.getId())) {
                return Optional.of(instructor2);
            }
            return Optional.empty();
        });

        Optional<Instructor> result = service.getMostActiveInstructor();

        assertThat(result).isPresent();
        Long resultId = result.get().getId();
        assertThat(counts.get(resultId)).isEqualTo(2L);
    }

    @Test
    void getMostActiveInstructor_whenInstructorNotFound_returnsEmptyOptional() {
        Map<Long, Long> counts = Map.of(99L, 10L);

        when(repository.countEnrollmentsPerInstructor()).thenReturn(counts);
        when(repository.findInstructorById(99L)).thenReturn(Optional.empty());

        Optional<Instructor> result = service.getMostActiveInstructor();

        assertThat(result).isEmpty();
    }

    // --- Tests for getInstructorsWithNoEnrollments ---

    @Test
    void getInstructorsWithNoEnrollments_whenAllHaveEnrollments_returnsEmptyList() {
        Map<Long, Long> counts = new HashMap<>();
        counts.put(instructor1.getId(), 2L);
        counts.put(instructor2.getId(), 1L);
        counts.put(instructor3.getId(), 3L);
        when(repository.countEnrollmentsPerInstructor()).thenReturn(counts);
        when(repository.findAllInstructors()).thenReturn(List.of(instructor1, instructor2, instructor3));

        List<Instructor> result = service.getInstructorsWithNoEnrollments();

        assertThat(result).isEmpty();
    }

    @Test
    void getInstructorsWithNoEnrollments_withSomeZero_returnsThoseInstructors() {
        Map<Long, Long> counts = new HashMap<>();
        counts.put(instructor1.getId(), 2L);
        counts.put(instructor2.getId(), 0L);
        // instructor3 Not in Map => 0 Implicitly
        when(repository.countEnrollmentsPerInstructor()).thenReturn(counts);
        when(repository.findAllInstructors()).thenReturn(List.of(instructor1, instructor2, instructor3));

        List<Instructor> result = service.getInstructorsWithNoEnrollments();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Instructor::getId).containsExactlyInAnyOrder(instructor2.getId(), instructor3.getId());
    }

    @Test
    void getInstructorsWithNoEnrollments_whenAllHaveZero_returnsAllInstructors() {
        // Empty Map - No Enrollments for Any Instructor
        when(repository.countEnrollmentsPerInstructor()).thenReturn(Collections.emptyMap());
        when(repository.findAllInstructors()).thenReturn(List.of(instructor1, instructor2, instructor3));

        List<Instructor> result = service.getInstructorsWithNoEnrollments();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Instructor::getId).containsExactlyInAnyOrder(instructor1.getId(), instructor2.getId(), instructor3.getId());
    }

    // --- Tests for getStudentEnrollments ---

    @Test
    void getStudentEnrollments_withValidStudent_returnsEnrollments() {
        Long studentId = 1L;
        List<Enrollment> expected = List.of(enrollment1, enrollment2);
        when(repository.findEnrollmentsByStudentId(studentId)).thenReturn(expected);

        List<Enrollment> result = service.getStudentEnrollments(studentId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getStudentEnrollments_withNoEnrollments_returnsEmptyList() {
        Long studentId = 99L;
        when(repository.findEnrollmentsByStudentId(studentId)).thenReturn(Collections.emptyList());

        List<Enrollment> result = service.getStudentEnrollments(studentId);

        assertThat(result).isEmpty();
    }
}