package com.gevernova.employeeleavetracker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Employee {
    private final String id;
    private String name;
    private int totalLeaves;
    private final List<Leave> appliedLeaves; // List of applied leaves

    public Employee(String name, int totalLeaves) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be null or blank.");
        }
        if (totalLeaves < 0) {
            throw new IllegalArgumentException("Total leaves cannot be negative.");
        }
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.totalLeaves = totalLeaves;
        this.appliedLeaves = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTotalLeaves() {
        return totalLeaves;
    }

    public List<Leave> getAppliedLeaves() {
        return Collections.unmodifiableList(appliedLeaves); // Return unmodifiable list
    }

    /**
     * Calculates the total number of days taken for leaves.
     * Uses Java 8 Stream to sum up durations.
     */
    public long getTakenLeaves() {
        return appliedLeaves.stream()
                .mapToLong(Leave::getDurationDays)
                .sum();
    }

    /**
     * Calculates the remaining leave balance.
     * Uses Java 8 Stream indirectly via getTakenLeaves().
     */
    public long getRemainingLeaves() {
        return totalLeaves - getTakenLeaves();
    }

    /**
     * Applies for a new leave.
     * @param leave The leave object to apply.
     * @throws LeaveLimitExceededException If the leave exceeds the remaining balance.
     * @throws InvalidLeaveDateException If the leave dates overlap with existing leaves.
     */
    public void applyForLeave(Leave leave) throws LeaveLimitExceededException, InvalidLeaveDateException {
        // Check for leave balance
        if (getRemainingLeaves() < leave.getDurationDays()) {
            throw new LeaveLimitExceededException("Cannot apply for " + leave.getDurationDays() + " days. Only " + getRemainingLeaves() + " leaves remaining.");
        }

        // Check for overlapping dates
        for (Leave existingLeave : appliedLeaves) {
            if (isOverlapping(existingLeave, leave)) {
                throw new InvalidLeaveDateException("Leave dates " + leave.getStartDate() + " to " + leave.getEndDate() +
                        " overlap with existing leave from " + existingLeave.getStartDate() +
                        " to " + existingLeave.getEndDate() + ".");
            }
        }

        appliedLeaves.add(leave);
        System.out.println(name + " applied for " + leave.getDurationDays() + " days of " + leave.getType() + " leave. Remaining: " + getRemainingLeaves());
    }

    private boolean isOverlapping(Leave existingLeave, Leave newLeave) {
        // Check if new leave starts before existing ends AND new leave ends after existing starts
        return newLeave.getStartDate().isBefore(existingLeave.getEndDate().plusDays(1)) &&
                newLeave.getEndDate().isAfter(existingLeave.getStartDate().minusDays(1));
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", totalLeaves=" + totalLeaves +
                ", takenLeaves=" + getTakenLeaves() +
                ", remainingLeaves=" + getRemainingLeaves() +
                ", appliedLeaves=" + appliedLeaves.size() + " records" +
                '}';
    }
}
