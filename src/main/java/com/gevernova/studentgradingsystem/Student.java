package com.gevernova.studentgradingsystem;

import java.util.List;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Student {
    private final String name;
    private final String id;
    private final List<Integer> marks;

    public Student(String name, String id, List<Integer> marks) throws InvalidMarkException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Student name cannot be null or empty.");
        }
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty.");
        }
        if (marks == null || marks.contains(null) || marks.stream().anyMatch(m -> m < 0 || m > 100)) {
            throw new InvalidMarkException("Invalid marks detected. Marks cannot be null, contain null, or be outside 0-100 range.");
        }
        this.name = name;
        this.id = id;
        this.marks = List.copyOf(marks); // Make marks list immutable
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Integer> getMarks() {
        return marks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", marks=" + marks +
                '}';
    }
}

