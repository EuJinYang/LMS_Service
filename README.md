# LMS Application

A Learning Management System (LMS) backend API built with Spring Boot and a companion desktop client built with Java Swing. The application manages students, instructors, courses, and enrollments, exposing four REST endpoints to retrieve relevant data. The desktop client connects to one of the endpoints (`/api/students/active`) to display a list of active students.

## Features

### Part A – REST API

- **F1 – Retrieve Student Enrollments**  
  `GET /api/students/{studentId}/enrollments`  
  Returns all course enrollments for a specific student.

- **F2 – List Active Students**  
  `GET /api/students/active`  
  Returns a list of students currently enrolled in at least one course.

- **F3 – Identify Most Active Instructor**  
  `GET /api/instructors/most-active`  
  Returns the instructor with the highest number of student enrollments.

- **F4 – List Instructors with No Enrollments**  
  `GET /api/instructors/no-enrollments`  
  Returns a list of instructors who currently have zero students enrolled in their courses.

### Part B – Desktop GUI Client

- Connects to the **F2** endpoint (`/api/students/active`).
- Allows the user to specify the API base URL (including port).
- Displays active students in a sortable, resizable table.
- Provides progress feedback, user‑friendly error messages, and a status bar with the last update time.

## Technologies Used

- **Backend:** Java 11, Spring Boot 2.7, Maven, JUnit 5, Mockito, Jackson
- **Frontend (Desktop):** Java Swing, SwingWorker, Jackson
- **Testing:** JUnit 5, Mockito, Spring Boot Test, AssertJ, JaCoCo (Code Coverage)
- **CI/CD:** GitHub Actions

## Project Structure
```
LMS_Service_Part_A/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com.example.lms/
    │   │       ├── controller/
    │   │       │   └── LmsController.java
    │   │       ├── models/
    │   │       │   └── LmsModels.java # All domain classes (Student, Instructor, Course, Enrollment)
    │   │       ├── repository/
    │   │       │   └── LmsRepository.java # In‑memory data store
    │   │       └── service/
    │   │       │   └── LmsService.java # Business logic
    │   │       └── LmsApplication.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/
            └── com.example.lms/
                ├── controller/
                │   └── LmsControllerTest.java # Unit tests for controller
                ├── service/
                │   └── LmsServiceTest.java # Unit tests for service (with mocks)
                └── LmsApplicationTests.java # Integration tests
```

Additionally, the Swing client resides in a separate module or can be placed in a sibling directory; for simplicity, its code is provided in the [`lms-swing-client`](#) folder.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- (Optional) Git

### Running the API (Part A)

1. Clone the repository:
   ```bash
   git clone https://github.com/EuJinYang/LMS_Service.git
   cd LMS_Service

2. Build and run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
   The API will be accessible at http://localhost:8080 by default.

### Running the Desktop Client (Part B)
1. Ensure the API is running.
2. Navigate to the Swing client directory and run:
   ```bash
   # If packaged as a jar
   java -jar lms-swing-client.jar
   ```
3. Enter the API Base URL (e.g., http://localhost:8080/api) and click "Load Active Students".

## Testing

### Automated Testing

The project uses a multi-level testing strategy following the V-Model, covering Unit, Integration, and System testing.

- **Unit Tests**  
  Located in `LmsServiceTest` and `LmsControllerTest`. These use Mockito to isolate business logic.
  
- **Integration Tests**  
  Located in `LmsApplicationTests`. These use a real in-memory repository to verify layer interactions.

To run all tests:
```bash
mvn clean test
```

### Continuous Integration

This project is configured with GitHub Actions. Every push to the main branch triggers an automated workflow that:

1. Sets up the Java environment.
2. Runs `mvn clean test` to execute all 27 unit and integration tests.
3. Verifies that both the Backend and Swing client compile correctly.

## Appendix

- GitHub Repository: https://github.com/EuJinYang/LMS_Service.git
- Demo Video: https://youtu.be/ytrguPLtIv4
