
import com.gevernova.employeeleavetracker.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Month;
import java.util.List;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeLeaveTrackerTest {

    private LeaveService leaveService;
    private Employee empOne;
    private Employee empTwo;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveService();
        empOne = new Employee("Alice Johnson", 20); // 20 total leaves
        empTwo = new Employee("Bob Williams", 10);  // 10 total leaves
        leaveService.addEmployee(empOne);
        leaveService.addEmployee(empTwo);
    }

    @Test
    @DisplayName("Should apply a valid leave and update balance")
    void shouldApplyValidLeaveAndUpdateBalance() {
        LocalDate startDate = LocalDate.of(2025, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(2025, Month.JANUARY, 5); // 5 days
        Leave casualLeave = new Leave(LeaveType.CASUAL, startDate, endDate);

        assertDoesNotThrow(() -> leaveService.applyLeave(empOne.getId(), casualLeave)); // Policy param removed
        assertEquals(15, empOne.getRemainingLeaves()); // 20 - 5 = 15
        assertEquals(1, empOne.getAppliedLeaves().size());
        assertEquals(5, empOne.getAppliedLeaves().get(0).getDurationDays());
    }

    @Test
    @DisplayName("Should throw LeaveLimitExceededException when balance is insufficient")
    void shouldThrowLeaveLimitExceededException() {
        LocalDate startDate = LocalDate.of(2025, Month.FEBRUARY, 1);
        LocalDate endDate = LocalDate.of(2025, Month.FEBRUARY, 15); // 15 days, emp2 has 10 total

        Leave sickLeave = new Leave(LeaveType.SICK, startDate, endDate);

        assertThrows(LeaveLimitExceededException.class, () ->
                leaveService.applyLeave(empTwo.getId(), sickLeave)); // Policy param removed
        assertEquals(10, empTwo.getRemainingLeaves()); // Balance should remain unchanged
        assertTrue(empTwo.getAppliedLeaves().isEmpty());
    }

    @Test
    @DisplayName("Should throw InvalidLeaveDateException for overlapping leaves")
    void shouldThrowInvalidLeaveDateExceptionForOverlapping() throws LeaveLimitExceededException, InvalidLeaveDateException {
        // First leave: Jan 5 - Jan 10 (6 days)
        Leave leaveOne = new Leave(LeaveType.CASUAL, LocalDate.of(2025, Month.JANUARY, 5), LocalDate.of(2025, Month.JANUARY, 10));
        leaveService.applyLeave(empOne.getId(), leaveOne); // Policy param removed

        // Overlapping leave: Jan 8 - Jan 12 (overlaps with leaveOne)
        Leave leaveTwo = new Leave(LeaveType.SICK, LocalDate.of(2025, Month.JANUARY, 8), LocalDate.of(2025, Month.JANUARY, 12));

        assertThrows(InvalidLeaveDateException.class, () ->
                leaveService.applyLeave(empOne.getId(), leaveTwo)); // Policy param removed
        assertEquals(14, empOne.getRemainingLeaves()); // Balance should reflect only the first leave (20 - 6 = 14)
        assertEquals(1, empOne.getAppliedLeaves().size()); // Only one leave applied
    }

    @Test
    @DisplayName("Should calculate remaining leaves correctly after multiple applications")
    void shouldCalculateRemainingLeavesCorrectly() throws LeaveLimitExceededException, InvalidLeaveDateException {
        leaveService.applyLeave(empOne.getId(), new Leave(LeaveType.CASUAL, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 3))); // Policy param removed
        leaveService.applyLeave(empOne.getId(), new Leave(LeaveType.SICK, LocalDate.of(2025, 4, 10), LocalDate.of(2025, 4, 11))); // Policy param removed

        assertEquals(15, empOne.getRemainingLeaves()); // 20 - 3 - 2 = 15
        assertEquals(2, empOne.getAppliedLeaves().size());
    }

    @Test
    @DisplayName("Should get employees with leave balance less than a threshold")
    void shouldGetEmployeesWithLowLeaveBalance() throws LeaveLimitExceededException, InvalidLeaveDateException {
        // emp1: 20 total, takes 16 -> 4 remaining
        leaveService.applyLeave(empOne.getId(), new Leave(LeaveType.ANNUAL, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 16))); // Policy param removed
        // emp2: 10 total, takes 7 -> 3 remaining
        leaveService.applyLeave(empTwo.getId(), new Leave(LeaveType.CASUAL, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 7))); // Policy param removed

        List<Employee> lowBalanceEmployees = leaveService.getEmployeesWithLowLeaveBalance(5); // Threshold 5

        assertEquals(2, lowBalanceEmployees.size());
        assertTrue(lowBalanceEmployees.stream().anyMatch(e -> e.getId().equals(empOne.getId())));
        assertTrue(lowBalanceEmployees.stream().anyMatch(e -> e.getId().equals(empTwo.getId())));
        assertEquals(4, lowBalanceEmployees.stream().filter(e -> e.getId().equals(empOne.getId())).findFirst().get().getRemainingLeaves());
        assertEquals(3, lowBalanceEmployees.stream().filter(e -> e.getId().equals(empTwo.getId())).findFirst().get().getRemainingLeaves());
    }

    // Removed tests related to custom LeavePolicy

    @Test
    @DisplayName("Should throw RuntimeException if employee not found during leave application")
    void shouldThrowRuntimeExceptionIfEmployeeNotFound() {
        LocalDate startDate = LocalDate.of(2025, Month.MARCH, 1);
        LocalDate endDate = LocalDate.of(2025, Month.MARCH, 5);
        Leave leave = new Leave(LeaveType.CASUAL, startDate, endDate);

        assertThrows(RuntimeException.class, () -> // This should ideally be EmployeeNotFoundException
                leaveService.applyLeave("non-existent-employee-id", leave)); // Policy param removed
    }
}
