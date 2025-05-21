package com.gevernova.employeeleavetracker;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        LeaveService leaveService = new LeaveService();

        System.out.println("--- Employee Leave Tracker Application ---");

        // 1. Add Employees
        Employee alice = new Employee("Alice Johnson", 20); // 20 total leaves
        Employee bob = new Employee("Bob Williams", 10);    // 10 total leaves
        Employee charlie = new Employee("Charlie Brown", 15); // 15 total leaves

        leaveService.addEmployee(alice);
        leaveService.addEmployee(bob);
        leaveService.addEmployee(charlie);

        System.out.println("\n--- Initial Employee Leave Balances ---");
        leaveService.getAllEmployees().forEach(System.out::println);

        // 2. Apply Leaves for Alice
        System.out.println("\n--- Alice applying for leaves ---");
        try {
            Leave aliceCasualLeave = new Leave(LeaveType.CASUAL, LocalDate.of(2025, Month.JUNE, 1), LocalDate.of(2025, Month.JUNE, 5)); // 5 days
            leaveService.applyLeave(alice.getId(), aliceCasualLeave);
            System.out.println("Alice's remaining leaves: " + alice.getRemainingLeaves());

            Leave aliceSickLeave = new Leave(LeaveType.SICK, LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12)); // 3 days
            leaveService.applyLeave(alice.getId(), aliceSickLeave);
            System.out.println("Alice's remaining leaves: " + alice.getRemainingLeaves());

            // Attempt to apply overlapping leave for Alice
            System.out.println("\n--- Attempting overlapping leave for Alice ---");
            Leave aliceOverlappingLeave = new Leave(LeaveType.ANNUAL, LocalDate.of(2025, Month.JUNE, 4), LocalDate.of(2025, Month.JUNE, 8)); // Overlaps with casual
            leaveService.applyLeave(alice.getId(), aliceOverlappingLeave); // This should throw InvalidLeaveDateException
        } catch (LeaveLimitExceededException | InvalidLeaveDateException | IllegalArgumentException e) {
            System.err.println("Error applying leave for Alice: " + e.getMessage());
        } catch (RuntimeException e) { // For Employee Not Found (if custom exception not added)
            System.err.println("Application Error: " + e.getMessage());
        }

        // 3. Apply Leaves for Bob (test exceeding limit)
        System.out.println("\n--- Bob applying for leaves (testing limits) ---");
        try {
            Leave bobLongLeave = new Leave(LeaveType.ANNUAL, LocalDate.of(2025, Month.AUGUST, 1), LocalDate.of(2025, Month.AUGUST, 15)); // 15 days (Bob has 10)
            leaveService.applyLeave(bob.getId(), bobLongLeave); // This should throw LeaveLimitExceededException
        } catch (LeaveLimitExceededException | InvalidLeaveDateException | IllegalArgumentException e) {
            System.err.println("Error applying leave for Bob: " + e.getMessage());
        }

        // 4. Get Employees with Low Leave Balance
        System.out.println("\n--- Employees with less than 5 leaves remaining ---");
        // Alice has 20 - 5 - 3 = 12 remaining
        // Bob has 10 remaining (failed to take 15 days)
        // Charlie has 15 remaining
        List<Employee> lowBalanceEmployees = leaveService.getEmployeesWithLowLeaveBalance(5); // Threshold 5
        if (lowBalanceEmployees.isEmpty()) {
            System.out.println("No employees with low leave balance found.");
        } else {
            lowBalanceEmployees.forEach(System.out::println); // This should be empty based on the current state.
        }

        // Let's make someone have low balance
        try {
            Leave charlieCasual = new Leave(LeaveType.CASUAL, LocalDate.of(2025, 9, 1), LocalDate.of(2025, 9, 12)); // 12 days
            leaveService.applyLeave(charlie.getId(), charlieCasual);
            System.out.println("Charlie's remaining leaves: " + charlie.getRemainingLeaves()); // 15 - 12 = 3

            System.out.println("\n--- Employees with less than 5 leaves remaining (after Charlie's leave) ---");
            lowBalanceEmployees = leaveService.getEmployeesWithLowLeaveBalance(5);
            lowBalanceEmployees.forEach(System.out::println); // Charlie should appear here
        } catch (LeaveLimitExceededException | InvalidLeaveDateException | IllegalArgumentException e) {
            System.err.println("Error applying leave for Charlie: " + e.getMessage());
        }

        System.out.println("\n--- Final Employee Leave Balances ---");
        leaveService.getAllEmployees().forEach(System.out::println);
    }
}
