package com.gevernova.booklibrarysystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User {
    private final String id;
    private String name;
    private final List<Book> borrowedBooks;
    private static final int MAX_BORROWED_BOOKS = 3; // Maximum books a user can borrow

    public User(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name cannot be null or blank.");
        }
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBorrowedBooks() {
        return Collections.unmodifiableList(borrowedBooks); // Return unmodifiable list
    }

    public int getNumberOfBorrowedBooks() {
        return borrowedBooks.size();
    }

    public boolean canBorrowMoreBooks() {
        return borrowedBooks.size() < MAX_BORROWED_BOOKS;
    }

    public void borrowBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if (borrowedBooks.size() >= MAX_BORROWED_BOOKS) {
            throw new IllegalStateException("User has reached the maximum borrowing limit of " + MAX_BORROWED_BOOKS + " books.");
        }
        if (borrowedBooks.contains(book)) {
            throw new IllegalStateException("User has already borrowed this book.");
        }
        borrowedBooks.add(book);
    }

    public void returnBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if (!borrowedBooks.remove(book)) {
            throw new IllegalStateException("User did not borrow this book.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id.substring(0, 8) + '\'' +
                ", name='" + name + '\'' +
                ", borrowedBooksCount=" + borrowedBooks.size() +
                '}';
    }
}
