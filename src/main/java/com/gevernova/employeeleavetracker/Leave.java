package com.gevernova.employeeleavetracker;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Leave {
    private LeaveType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private long durationDays; // Duration in days

    public Leave(LeaveType type, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date and end date must be valid, and start date cannot be after end date.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Leave type cannot be null.");
        }
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Inclusive of start and end dates
    }

    public LeaveType getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public long getDurationDays() {
        return durationDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leave leave = (Leave) o;
        return durationDays == leave.durationDays &&
                type == leave.type &&
                Objects.equals(startDate, leave.startDate) &&
                Objects.equals(endDate, leave.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, startDate, endDate, durationDays);
    }

    @Override
    public String toString() {
        return "Leave{" +
                "type=" + type +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", durationDays=" + durationDays +
                '}';
    }
}
