package com.gevernova.studentgradingsystem;

@FunctionalInterface
public interface GradingStrategy {
    String assignGrade(double average);
}
