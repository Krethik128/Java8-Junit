package com.gevernova.studentgradingsystem;

import java.util.Arrays;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        GradingService gradingService = new GradingService();

        // Define a standard grading strategy
        GradingStrategy standardGrading = average -> {
            if (average >= 90) return "A";
            if (average >= 80) return "B";
            if (average >= 70) return "C";
            if (average >= 60) return "D";
            return "F";
        };

        System.out.println("--- Student Grading System Demo ---");

        // Valid Student Example
        try {
            Student alice = new Student("Alice", "S001", Arrays.asList(85, 90, 78));
            System.out.println("Student: " + alice.getName() + " (ID: " + alice.getId() + ")");
            double aliceAverage = gradingService.calculateAverage(alice);
            String aliceGrade = gradingService.getGrade(alice, standardGrading);
            System.out.printf("  Average: %.2f, Grade: %s%n", aliceAverage, aliceGrade);
        } catch (InvalidMarkException | EmptyMarksListException e) {
            System.err.println("Error for Alice: " + e.getMessage());
        }

        // Student with different marks
        try {
            Student bob = new Student("Bob", "S002", Arrays.asList(60, 65, 70));
            System.out.println("\nStudent: " + bob.getName() + " (ID: " + bob.getId() + ")");
            double bobAverage = gradingService.calculateAverage(bob);
            String bobGrade = gradingService.getGrade(bob, standardGrading);
            System.out.printf("  Average: %.2f, Grade: %s%n", bobAverage, bobGrade);
        } catch (InvalidMarkException | EmptyMarksListException e) {
            System.err.println("Error for Bob: " + e.getMessage());
        }

        // --- Exception Handling Demonstrations ---

        // Case 1: Student with invalid marks (e.g., negative)
        System.out.println("\n--- Testing Invalid Marks ---");
        try {
            new Student("Charlie", "S003", Arrays.asList(90, 75, -10));
            System.out.println("Student Charlie created (should have failed)!");
        } catch (InvalidMarkException e) {
            System.err.println("Caught expected error creating Charlie: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Caught unexpected argument error creating Charlie: " + e.getMessage());
        }

        // Case 2: Student with empty marks list
        System.out.println("\n--- Testing Empty Marks List ---");
        try {
            Student david = new Student("David", "S004", Collections.emptyList());
            System.out.println("Student: " + david.getName() + " (ID: " + david.getId() + ")");
            gradingService.calculateAverage(david);
        } catch (InvalidMarkException e) {
            System.err.println("Caught unexpected error creating David: " + e.getMessage());
        } catch (EmptyMarksListException e) {
            System.err.println("Caught expected error for David's average: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Caught unexpected argument error creating David: " + e.getMessage());
        }

        // Case 3: Student with null name
        System.out.println("\n--- Testing Null Student Name ---");
        try {
            new Student(null, "S005", Arrays.asList(80, 85));
            System.out.println("Student created with null name (should have failed)!");
        } catch (InvalidMarkException e) {
            System.err.println("Caught unexpected error creating null name student: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Caught expected argument error creating null name student: " + e.getMessage());
        }
    }
}
