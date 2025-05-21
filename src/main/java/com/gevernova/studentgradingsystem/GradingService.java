package com.gevernova.studentgradingsystem;

public class GradingService {
    public double calculateAverage(Student student) throws EmptyMarksListException {
        if (student.getMarks().isEmpty()) {
            throw new EmptyMarksListException("No marks found for student " + student.getName() + " (ID: " + student.getId() + ").");
        }
        return student.getMarks().stream().mapToInt(i -> i).average().orElse(0);
    }

    public String getGrade(Student student, GradingStrategy strategy) throws EmptyMarksListException {
        return strategy.assignGrade(calculateAverage(student));
    }
}

