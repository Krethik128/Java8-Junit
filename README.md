# Java8-Junit

This repository contains a collection of Java 8 projects, each focusing on different aspects of modern Java development, including object-oriented design, functional programming with Streams and Optional, and robust testing with JUnit 5.

Each project is designed to tackle a common software engineering problem, demonstrating best practices in exception handling, data management, and test-driven development.

## Projects Included

This repository contains the following five distinct projects:

1.  **Library Book Lending System (`booklibrarysystem`)**
2.  **Employee Leave Tracker (`employeeleavetracker`)**
3.  **Inventory Management System (`inventorymanagemntsystem`)**
4.  **Online Order Processing (`onlineorderprocessing`)**
5.  **Student Grading System (`studentgradingsystem`)**

---

### 1. Library Book Lending System

A system for managing book borrowing and returning in a library.

* **Scenario:** A library maintains a system where users borrow and return books.
* **Key Features:**
    * `Book` and `User` classes.
    * Users can borrow up to 3 books.
    * Stream-based listing of available and borrowed books.
    * Filtering books by author and genre using Streams.
    * `Optional` for handling missing book entries.
    * Custom Exceptions: `BookUnavailableException`, `BookLimitExceededException`.
* **JUnit Tests:** Covers borrowing, returning, and edge cases (no books left, limit exceeded).

### 2. Employee Leave Tracker

A system to manage employee leave requests and approvals.

* **Scenario:** An organization needs a system to track employee leave applications.
* **Key Features:** (Assumed based on common requirements for such a system)
    * `Employee` and `LeaveRequest` classes.
    * Managing different leave types (e.g., Sick, Casual, Annual).
    * Status tracking for leave requests (e.g., Pending, Approved, Rejected).
    * Basic validation for leave applications (e.g., sufficient leave balance).
    * Stream-based reporting (e.g., listing all pending leaves, leaves by employee).
    * Custom Exceptions: (e.g., `InsufficientLeaveBalanceException`, `InvalidLeaveRequestException`).
* **JUnit Tests:** Testing leave application, approval/rejection, and balance updates.

### 3. Inventory Management System

A core system for managing product inventory in a retail store.

* **Scenario:** A retail store needs to manage its product inventory efficiently.
* **Key Features:**
    * `Product` class with `id`, `name`, `category`, `quantity`, `price`.
    * `ProductCategory` enum.
    * CRUD operations (Add, Update, Remove, Find) for products.
    * Filtering products by category, low stock, and searching by name.
    * Sorting products by category and price.
    * `Optional` for finding products.
    * Custom Exceptions: `InvalidProductException`, `ProductNotFoundException`.
* **JUnit Tests:** Comprehensive testing of CRUD, search, filter, and sort functionalities, including edge cases and exception handling.

### 4. Online Order Processing

A system to validate and process online customer orders.

* **Scenario:** An e-commerce platform processes customer orders, requiring robust validation.
* **Key Features:**
    * `Order` class with `user`, `items`, `paymentMethod`, `deliveryAddress`, `promoCode`.
    * `OrderValidator` to ensure order integrity.
    * `Optional` for handling optional fields like `promoCode`.
    * Custom Exceptions: `InvalidPaymentException`, `InvalidAddressException`.
* **JUnit Tests:** Testing order creation, validation rules, and proper exception throwing for invalid orders.

### Student Grading System

A simple system for calculating student averages and assigning grades based on different strategies.

* **Scenario:** A basic setup to process student marks and determine their academic grades.
* **Key Features:**
  * `Student` class to hold student details and a list of marks.
  * `GradingService` to calculate the average marks for a student.
  * `GradingStrategy` functional interface to define custom grading logic (e.g., standard letter grades, pass/fail).
  * Custom Exceptions: `InvalidMarkException` for invalid mark values, `EmptyMarksListException` for students with no recorded marks.
* **JUnit Tests:** Covers the core functionalities of calculating averages and assigning grades, including various mark scenarios and exception handling.
* **Main Function:** Provides a demonstration of how to create students, calculate their grades, and handle expected errors.

---

## Technologies Used

* **Java 8+:** Core programming language features (Streams, Optional, Lambdas).
* **JUnit 5:** Testing framework for unit and integration tests.
* **Maven (Recommended):** For project structure, dependency management, and build automation.

