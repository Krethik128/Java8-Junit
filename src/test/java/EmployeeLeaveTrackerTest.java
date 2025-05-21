
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
    private Employee emp1;
    private Employee emp2;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveService();
        emp1 = new Employee("Alice Johnson", 20); // 20 total leaves
        emp2 = new Employee("Bob Williams", 10);  // 10 total leaves
        leaveService.addEmployee(emp1);
        leaveService.addEmployee(emp2);
    }

    @Test
    @DisplayName("Should apply a valid leave and update balance")
    void shouldApplyValidLeaveAndUpdateBalance() {
        LocalDate startDate = LocalDate.of(2025, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(2025, Month.JANUARY, 5); // 5 days
        Leave casualLeave = new Leave(LeaveType.CASUAL, startDate, endDate);

        assertDoesNotThrow(() -> leaveService.applyLeave(emp1.getId(), casualLeave)); // Policy param removed
        assertEquals(15, emp1.getRemainingLeaves()); // 20 - 5 = 15
        assertEquals(1, emp1.getAppliedLeaves().size());
        assertEquals(5, emp1.getAppliedLeaves().get(0).getDurationDays());
    }

    @Test
    @DisplayName("Should throw LeaveLimitExceededException when balance is insufficient")
    void shouldThrowLeaveLimitExceededException() {
        LocalDate startDate = LocalDate.of(2025, Month.FEBRUARY, 1);
        LocalDate endDate = LocalDate.of(2025, Month.FEBRUARY, 15); // 15 days, emp2 has 10 total

        Leave sickLeave = new Leave(LeaveType.SICK, startDate, endDate);

        assertThrows(LeaveLimitExceededException.class, () ->
                leaveService.applyLeave(emp2.getId(), sickLeave)); // Policy param removed
        assertEquals(10, emp2.getRemainingLeaves()); // Balance should remain unchanged
        assertTrue(emp2.getAppliedLeaves().isEmpty());
    }

    @Test
    @DisplayName("Should throw InvalidLeaveDateException for overlapping leaves")
    void shouldThrowInvalidLeaveDateExceptionForOverlapping() throws LeaveLimitExceededException, InvalidLeaveDateException {
        // First leave: Jan 5 - Jan 10 (6 days)
        Leave leave1 = new Leave(LeaveType.CASUAL, LocalDate.of(2025, Month.JANUARY, 5), LocalDate.of(2025, Month.JANUARY, 10));
        leaveService.applyLeave(emp1.getId(), leave1); // Policy param removed

        // Overlapping leave: Jan 8 - Jan 12 (overlaps with leave1)
        Leave leave2 = new Leave(LeaveType.SICK, LocalDate.of(2025, Month.JANUARY, 8), LocalDate.of(2025, Month.JANUARY, 12));

        assertThrows(InvalidLeaveDateException.class, () ->
                leaveService.applyLeave(emp1.getId(), leave2)); // Policy param removed
        assertEquals(14, emp1.getRemainingLeaves()); // Balance should reflect only the first leave (20 - 6 = 14)
        assertEquals(1, emp1.getAppliedLeaves().size()); // Only one leave applied
    }

    @Test
    @DisplayName("Should calculate remaining leaves correctly after multiple applications")
    void shouldCalculateRemainingLeavesCorrectly() throws LeaveLimitExceededException, InvalidLeaveDateException {
        leaveService.applyLeave(emp1.getId(), new Leave(LeaveType.CASUAL, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 3))); // Policy param removed
        leaveService.applyLeave(emp1.getId(), new Leave(LeaveType.SICK, LocalDate.of(2025, 4, 10), LocalDate.of(2025, 4, 11))); // Policy param removed

        assertEquals(15, emp1.getRemainingLeaves()); // 20 - 3 - 2 = 15
        assertEquals(2, emp1.getAppliedLeaves().size());
    }

    @Test
    @DisplayName("Should get employees with leave balance less than a threshold")
    void shouldGetEmployeesWithLowLeaveBalance() throws LeaveLimitExceededException, InvalidLeaveDateException {
        // emp1: 20 total, takes 16 -> 4 remaining
        leaveService.applyLeave(emp1.getId(), new Leave(LeaveType.ANNUAL, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 16))); // Policy param removed
        // emp2: 10 total, takes 7 -> 3 remaining
        leaveService.applyLeave(emp2.getId(), new Leave(LeaveType.CASUAL, LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 7))); // Policy param removed

        List<Employee> lowBalanceEmployees = leaveService.getEmployeesWithLowLeaveBalance(5); // Threshold 5

        assertEquals(2, lowBalanceEmployees.size());
        assertTrue(lowBalanceEmployees.stream().anyMatch(e -> e.getId().equals(emp1.getId())));
        assertTrue(lowBalanceEmployees.stream().anyMatch(e -> e.getId().equals(emp2.getId())));
        assertEquals(4, lowBalanceEmployees.stream().filter(e -> e.getId().equals(emp1.getId())).findFirst().get().getRemainingLeaves());
        assertEquals(3, lowBalanceEmployees.stream().filter(e -> e.getId().equals(emp2.getId())).findFirst().get().getRemainingLeaves());
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
