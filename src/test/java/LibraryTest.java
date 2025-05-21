import com.gevernova.booklibrarysystem.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private Library library;
    private Book book1, book2, book3, book4, book5;
    private User user1, user2;

    @BeforeEach
    void setUp() {
        library = new Library();

        book1 = new Book("The Lord of the Rings", "J.R.R. Tolkien", "Fantasy");
        book2 = new Book("Pride and Prejudice", "Jane Austen", "Romance");
        book3 = new Book("1984", "George Orwell", "Dystopian");
        book4 = new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy");
        book5 = new Book("To Kill a Mockingbird", "Harper Lee", "Fiction");

        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        library.addBook(book4);
        library.addBook(book5);

        user1 = new User("Alice");
        user2 = new User("Bob");

        library.addUser(user1);
        library.addUser(user2);
    }

    @Test
    @DisplayName("Should successfully borrow a book")
    void shouldSuccessfullyBorrowBook() {
        assertDoesNotThrow(() -> library.borrowBook(user1.getId(), book1.getId()));
        assertTrue(book1.isBorrowed());
        assertTrue(user1.getBorrowedBooks().contains(book1));
        assertEquals(1, user1.getNumberOfBorrowedBooks());
        assertEquals(4, library.getAvailableBooks().size());
        assertEquals(1, library.getBorrowedBooks().size());
    }

    @Test
    @DisplayName("Should throw BookUnavailableException if book is already borrowed")
    void shouldThrowBookUnavailableExceptionIfBookAlreadyBorrowed() {
        library.borrowBook(user1.getId(), book1.getId()); // User1 borrows book1

        assertThrows(BookUnavailableException.class, () -> library.borrowBook(user2.getId(), book1.getId()));
        assertTrue(book1.isBorrowed()); // Still borrowed by user1
        assertFalse(user2.getBorrowedBooks().contains(book1)); // User2 did not get it
    }

    @Test
    @DisplayName("Should throw BookLimitExceededException if user exceeds limit")
    void shouldThrowBookLimitExceededExceptionIfUserExceedsLimit() {
        // User1 borrows 3 books
        library.borrowBook(user1.getId(), book1.getId());
        library.borrowBook(user1.getId(), book2.getId());
        library.borrowBook(user1.getId(), book3.getId());

        assertEquals(3, user1.getNumberOfBorrowedBooks());

        // User1 tries to borrow a 4th book
        assertThrows(BookLimitExceededException.class, () -> library.borrowBook(user1.getId(), book4.getId()));
        assertFalse(book4.isBorrowed()); // Book4 should not be borrowed
        assertFalse(user1.getBorrowedBooks().contains(book4));
        assertEquals(3, user1.getNumberOfBorrowedBooks()); // Still 3 books
    }

    @Test
    @DisplayName("Should successfully return a borrowed book")
    void shouldSuccessfullyReturnBook() {
        library.borrowBook(user1.getId(), book1.getId());
        assertTrue(book1.isBorrowed());
        assertEquals(1, user1.getNumberOfBorrowedBooks());

        assertDoesNotThrow(() -> library.returnBook(user1.getId(), book1.getId()));
        assertFalse(book1.isBorrowed());
        assertFalse(user1.getBorrowedBooks().contains(book1));
        assertEquals(0, user1.getNumberOfBorrowedBooks());
        assertEquals(5, library.getAvailableBooks().size());
        assertEquals(0, library.getBorrowedBooks().size());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if returning a book not borrowed by user")
    void shouldThrowIllegalArgumentExceptionIfReturningBookNotBorrowedByUser() {
        library.borrowBook(user1.getId(), book1.getId()); // User1 borrows book1

        // User2 tries to return book1 (which they didn't borrow)
        assertThrows(IllegalArgumentException.class, () -> library.returnBook(user2.getId(), book1.getId()));
        assertTrue(book1.isBorrowed()); // Still borrowed by user1
        assertTrue(user1.getBorrowedBooks().contains(book1));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if returning an unborrowed book")
    void shouldThrowIllegalArgumentExceptionIfReturningUnborrowedBook() {
        // book2 is not borrowed by anyone
        assertFalse(book2.isBorrowed());
        assertThrows(IllegalArgumentException.class, () -> library.returnBook(user1.getId(), book2.getId()));
        assertFalse(book2.isBorrowed());
    }

    @Test
    @DisplayName("Should list all available books correctly")
    void shouldListAvailableBooksCorrectly() {
        library.borrowBook(user1.getId(), book1.getId());
        library.borrowBook(user2.getId(), book2.getId());

        List<Book> availableBooks = library.getAvailableBooks();
        assertEquals(3, availableBooks.size()); // 5 total - 2 borrowed = 3 available
        assertFalse(availableBooks.contains(book1));
        assertFalse(availableBooks.contains(book2));
        assertTrue(availableBooks.contains(book3));
        assertTrue(availableBooks.contains(book4));
        assertTrue(availableBooks.contains(book5));
    }

    @Test
    @DisplayName("Should list all borrowed books correctly")
    void shouldListBorrowedBooksCorrectly() {
        library.borrowBook(user1.getId(), book1.getId());
        library.borrowBook(user2.getId(), book2.getId());

        List<Book> borrowedBooks = library.getBorrowedBooks();
        assertEquals(2, borrowedBooks.size());
        assertTrue(borrowedBooks.contains(book1));
        assertTrue(borrowedBooks.contains(book2));
        assertFalse(borrowedBooks.contains(book3));
    }

    @Test
    @DisplayName("Should filter books by author correctly")
    void shouldFilterBooksByAuthorCorrectly() {
        List<Book> tolkienBooks = library.findBooksByAuthor("Tolkien");
        assertEquals(2, tolkienBooks.size());
        assertTrue(tolkienBooks.contains(book1)); // Lord of the Rings
        assertTrue(tolkienBooks.contains(book4)); // The Hobbit

        List<Book> austenBooks = library.findBooksByAuthor("Jane Austen");
        assertEquals(1, austenBooks.size());
        assertTrue(austenBooks.contains(book2));
    }

    @Test
    @DisplayName("Should filter books by genre correctly")
    void shouldFilterBooksByGenreCorrectly() {
        List<Book> fantasyBooks = library.findBooksByGenre("Fantasy");
        assertEquals(2, fantasyBooks.size());
        assertTrue(fantasyBooks.contains(book1));
        assertTrue(fantasyBooks.contains(book4));

        List<Book> romanceBooks = library.findBooksByGenre("Romance");
        assertEquals(1, romanceBooks.size());
        assertTrue(romanceBooks.contains(book2));
    }

    @Test
    @DisplayName("Should return Optional.of(Book) if book found by ID")
    void shouldReturnOptionalOfBookIfBookFoundById() {
        Optional<Book> foundBook = library.findBookById(book3.getId());
        assertTrue(foundBook.isPresent());
        assertEquals(book3, foundBook.get());
    }

    @Test
    @DisplayName("Should return Optional.empty() if book not found by ID")
    void shouldReturnOptionalEmptyIfBookNotFoundById() {
        Optional<Book> foundBook = library.findBookById("non-existent-id");
        assertFalse(foundBook.isPresent());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if user not found during borrow")
    void shouldThrowIllegalArgumentExceptionIfUserNotFoundBorrow() {
        assertThrows(IllegalArgumentException.class, () -> library.borrowBook("non-existent-user", book1.getId()));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if book not found during borrow")
    void shouldThrowIllegalArgumentExceptionIfBookNotFoundBorrow() {
        assertThrows(IllegalArgumentException.class, () -> library.borrowBook(user1.getId(), "non-existent-book"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if user not found during return")
    void shouldThrowIllegalArgumentExceptionIfUserNotFoundReturn() {
        library.borrowBook(user1.getId(), book1.getId());
        assertThrows(IllegalArgumentException.class, () -> library.returnBook("non-existent-user", book1.getId()));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if book not found during return")
    void shouldThrowIllegalArgumentExceptionIfBookNotFoundReturn() {
        library.borrowBook(user1.getId(), book1.getId());
        assertThrows(IllegalArgumentException.class, () -> library.returnBook(user1.getId(), "non-existent-book"));
    }
}
