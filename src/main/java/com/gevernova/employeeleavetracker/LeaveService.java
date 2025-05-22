package com.gevernova.employeeleavetracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LeaveService {
    private final List<Employee> employees;

    public LeaveService() {
        this.employees = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        employees.add(employee);
        System.out.println("Employee added: " + employee.getName());
    }

    public Optional<Employee> findEmployeeById(String employeeId) {
        return employees.stream()
                .filter(e -> e.getId().equals(employeeId))
                .findFirst();
    }

    /**
     * Applies leave for a specific employee.
     */
    public void applyLeave(String employeeId, Leave leave) // Removed LeavePolicy parameter
            throws LeaveLimitExceededException, InvalidLeaveDateException, RuntimeException {
        Employee employee = findEmployeeById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee with ID " + employeeId + " not found.")); // Consider a custom EmployeeNotFoundException

        // No LeavePolicy check here, as per user's request.
        employee.applyForLeave(leave);
    }

    /**
     * Gets employees whose remaining leave balance is less than a specified threshold.
     * Uses Java 8 Stream to filter employees.
     */
    public List<Employee> getEmployeesWithLowLeaveBalance(long threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative.");
        }
        return employees.stream()
                .filter(employee -> employee.getRemainingLeaves() < threshold)
                .collect(Collectors.toList());
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees); // Return a copy
    }
}
