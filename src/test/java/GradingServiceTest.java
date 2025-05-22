import com.gevernova.studentgradingsystem.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class GradingServiceTest {

    private GradingService gradingService;
    private GradingStrategy standardGradingStrategy;
    private GradingStrategy passFailGradingStrategy;

    @BeforeEach
    void setUp() {
        gradingService = new GradingService();
        standardGradingStrategy = average -> {
            if (average >= 90) return "A";
            if (average >= 80) return "B";
            if (average >= 70) return "C";
            if (average >= 60) return "D";
            return "F";
        };
        passFailGradingStrategy = average -> average >= 50 ? "Pass" : "Fail";
    }

    // --- Student Class Tests ---

    @Test
    @DisplayName("Should create Student with valid details")
    void shouldCreateStudentWithValidDetails() {
        assertDoesNotThrow(() -> {
            new Student("Test Student", "T001", Arrays.asList(70, 80, 90));
        });
        Student student = assertDoesNotThrow(() -> new Student("Valid User", "ID123", Arrays.asList(100, 90, 80)));
        assertEquals("Valid User", student.getName());
        assertEquals("ID123", student.getId());
        assertEquals(Arrays.asList(100, 90, 80), student.getMarks());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if student name is null")
    void shouldThrowIllegalArgumentExceptionIfStudentNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Student(null, "T002", Arrays.asList(70, 80, 90)));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if student name is empty")
    void shouldThrowIllegalArgumentExceptionIfStudentNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                new Student("", "T003", Arrays.asList(70, 80, 90)));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if student ID is null")
    void shouldThrowIllegalArgumentExceptionIfStudentIDIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Student("Test Student", null, Arrays.asList(70, 80, 90)));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if student ID is empty")
    void shouldThrowIllegalArgumentExceptionIfStudentIDIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                new Student("Test Student", "", Arrays.asList(70, 80, 90)));
    }

    @Test
    @DisplayName("Should throw InvalidMarkException if marks list is null")
    void shouldThrowInvalidMarkExceptionIfMarksListIsNull() {
        assertThrows(InvalidMarkException.class, () ->
                new Student("Test Student", "T004", null));
    }

    @Test
    @DisplayName("Should throw InvalidMarkException if marks list contains null")
    void shouldThrowInvalidMarkExceptionIfMarksListContainsNull() {
        assertThrows(InvalidMarkException.class, () ->
                new Student("Test Student", "T005", Arrays.asList(70, null, 90)));
    }

    @Test
    @DisplayName("Should throw InvalidMarkException if a mark is negative")
    void shouldThrowInvalidMarkExceptionIfMarkIsNegative() {
        assertThrows(InvalidMarkException.class, () ->
                new Student("Test Student", "T006", Arrays.asList(70, 80, -5)));
    }

    @Test
    @DisplayName("Should throw InvalidMarkException if a mark is greater than 100")
    void shouldThrowInvalidMarkExceptionIfMarkIsGreaterThan100() {
        assertThrows(InvalidMarkException.class, () ->
                new Student("Test Student", "T007", Arrays.asList(70, 101, 90)));
    }

    // --- GradingService Tests ---

    @Test
    @DisplayName("Should calculate average correctly for a list of marks")
    void shouldCalculateAverageCorrectly() throws InvalidMarkException, EmptyMarksListException {
        Student student = new Student("Alice", "A001", Arrays.asList(80, 90, 70));
        assertEquals(80.0, gradingService.calculateAverage(student), 0.001);

        Student studentTwo = new Student("Bob", "B001", Arrays.asList(100, 100, 100));
        assertEquals(100.0, gradingService.calculateAverage(studentTwo), 0.001);

        Student studentThree = new Student("Charlie", "C001", Arrays.asList(50, 50));
        assertEquals(50.0, gradingService.calculateAverage(studentThree), 0.001);
    }

    @Test
    @DisplayName("Should throw EmptyMarksListException if marks list is empty when calculating average")
    void shouldThrowEmptyMarksListExceptionIfMarksListIsEmptyForAverage() throws InvalidMarkException {
        Student student = new Student("David", "D001", Collections.emptyList());
        assertThrows(EmptyMarksListException.class, () -> gradingService.calculateAverage(student));
    }

    @Test
    @DisplayName("Should assign grade correctly using standard grading strategy")
    void shouldAssignGradeCorrectlyWithStandardStrategy() throws InvalidMarkException, EmptyMarksListException {
        Student studentA = new Student("Student A", "SA001", Arrays.asList(95, 90, 92)); // Average ~92.33 -> A
        assertEquals("A", gradingService.getGrade(studentA, standardGradingStrategy));

        Student studentB = new Student("Student B", "SB001", Arrays.asList(82, 88, 80)); // Average ~83.33 -> B
        assertEquals("B", gradingService.getGrade(studentB, standardGradingStrategy));

        Student studentC = new Student("Student C", "SC001", Arrays.asList(70, 75, 68)); // Average ~71.00 -> C
        assertEquals("C", gradingService.getGrade(studentC, standardGradingStrategy));

        Student studentD = new Student("Student D", "SD001", Arrays.asList(60, 65, 62)); // Average ~62.33 -> D
        assertEquals("D", gradingService.getGrade(studentD, standardGradingStrategy));

        Student studentF = new Student("Student F", "SF001", Arrays.asList(40, 50, 30)); // Average ~40.00 -> F
        assertEquals("F", gradingService.getGrade(studentF, standardGradingStrategy));
    }

    @Test
    @DisplayName("Should assign grade correctly using pass/fail grading strategy")
    void shouldAssignGradeCorrectlyWithPassFailStrategy() throws InvalidMarkException, EmptyMarksListException {
        Student studentPass = new Student("Student Pass", "SP001", Arrays.asList(70, 80, 90)); // Average 80 -> Pass
        assertEquals("Pass", gradingService.getGrade(studentPass, passFailGradingStrategy));

        Student studentFail = new Student("Student Fail", "SF002", Arrays.asList(30, 40, 45)); // Average ~38.33 -> Fail
        assertEquals("Fail", gradingService.getGrade(studentFail, passFailGradingStrategy));
    }

    @Test
    @DisplayName("Should throw EmptyMarksListException if marks list is empty when getting grade")
    void shouldThrowEmptyMarksListExceptionIfMarksListIsEmptyForGrade() throws InvalidMarkException {
        Student student = new Student("Eve", "E001", Collections.emptyList());
        assertThrows(EmptyMarksListException.class, () -> gradingService.getGrade(student, standardGradingStrategy));
    }
}


