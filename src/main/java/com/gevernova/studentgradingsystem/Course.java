package com.gevernova.studentgradingsystem;

import java.util.Objects;
import java.util.UUID;

public class Course {
    private final String id;
    private final String name;
    private final String code;

    public Course(String name, String code) {
        if (name == null || name.isBlank() || code == null || code.isBlank()) {
            throw new IllegalArgumentException("Course name and code cannot be null or empty.");
        }
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
