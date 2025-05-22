package com.gevernova.booklibrarysystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Library {
    private final Map<String, Book> books; // Stores all books by ID
    private final Map<String, User> users; // Stores all users by ID

    public Library() {
        this.books = new HashMap<>();
        this.users = new HashMap<>();
    }

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        books.put(book.getId(), book);
        System.out.println("Added book: " + book.getTitle());
    }

    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        users.put(user.getId(), user);
        System.out.println("Added user: " + user.getName());
    }

    /**
     * Allows a user to borrow a book.
     */
    public void borrowBook(String userId, String bookId) throws BookUnavailableException, BookLimitExceededException {
        User user = Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));
        Book book = Optional.ofNullable(books.get(bookId))
                .orElseThrow(() -> new IllegalArgumentException("Book with ID " + bookId + " not found."));

        if (book.isBorrowed()) {
            throw new BookUnavailableException("Book '" + book.getTitle() + "' is currently unavailable.");
        }
        if (!user.canBorrowMoreBooks()) {
            throw new BookLimitExceededException("User '" + user.getName() + "' cannot borrow more books. Limit reached.");
        }

        book.setBorrowed(true);
        user.borrowBook(book);
        System.out.println(user.getName() + " borrowed '" + book.getTitle() + "'.");
    }

    /**
     * Allows a user to return a book.
     */
    public void returnBook(String userId, String bookId) {
        User user = Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));
        Book book = Optional.ofNullable(books.get(bookId))
                .orElseThrow(() -> new IllegalArgumentException("Book with ID " + bookId + " not found."));

        if (!book.isBorrowed()) {
            throw new IllegalArgumentException("Book '" + book.getTitle() + "' was not borrowed.");
        }
        if (!user.getBorrowedBooks().contains(book)) {
            throw new IllegalArgumentException("User '" + user.getName() + "' did not borrow '" + book.getTitle() + "'.");
        }

        book.setBorrowed(false);
        user.returnBook(book);
        System.out.println(user.getName() + " returned '" + book.getTitle() + "'.");
    }

    /**
     * Lists all available books.
     * @return A list of books that are not currently borrowed.
     */
    public List<Book> getAvailableBooks() {
        return books.values().stream()
                .filter(book -> !book.isBorrowed())
                .collect(Collectors.toList());
    }

    /**
     * Lists all borrowed books.
     * @return A list of books that are currently borrowed.
     */
    public List<Book> getBorrowedBooks() {
        return books.values().stream()
                .filter(Book::isBorrowed)
                .collect(Collectors.toList());
    }

    /**
     * Filters books by author (case-insensitive, partial match).
     */
    public List<Book> findBooksByAuthor(String author) {
        if (author == null) return new ArrayList<>();
        return books.values().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Filters books by genre (case-insensitive, partial match).
     */
    public List<Book> findBooksByGenre(String genre) {
        if (genre == null) return new ArrayList<>();
        return books.values().stream()
                .filter(book -> book.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Finds a book by its ID using Optional.
     */
    public Optional<Book> findBookById(String bookId) {
        return Optional.ofNullable(books.get(bookId));
    }

    /**
     * Finds a user by their ID using Optional.
     */
    public Optional<User> findUserById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}