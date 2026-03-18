package com.example.lms.models;

import java.time.LocalDate;
import java.util.Objects;

public final class LmsModels {

    private LmsModels() {} // Prevent Instantiation

    public static class Student {
        private Long id;
        private String name;
        private String email;

        // Constructors
        public Student() {}
        public Student(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Student student = (Student) o;
            return Objects.equals(id, student.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static class Instructor {
        private Long id;
        private String name;
        private String email;

        // Constructors
        public Instructor() {}
        public Instructor(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Instructor that = (Instructor) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static class Course {
        private Long id;
        private String name;
        private Long instructorId;  // Reference to Instructor

        // Constructors
        public Course() {}
        public Course(Long id, String name, Long instructorId) {
            this.id = id;
            this.name = name;
            this.instructorId = instructorId;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Long getInstructorId() { return instructorId; }
        public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Course course = (Course) o;
            return Objects.equals(id, course.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static class Enrollment {
        private Long id;
        private Long studentId;
        private Long courseId;
        private LocalDate enrollmentDate;

        // Constructors
        public Enrollment() {}
        public Enrollment(Long id, Long studentId, Long courseId, LocalDate enrollmentDate) {
            this.id = id;
            this.studentId = studentId;
            this.courseId = courseId;
            this.enrollmentDate = enrollmentDate;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }

        public LocalDate getEnrollmentDate() { return enrollmentDate; }
        public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Enrollment that = (Enrollment) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}