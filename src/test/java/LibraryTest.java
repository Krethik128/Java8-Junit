import com.gevernova.booklibrarysystem.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private Library library;
    private Book bookOne, bookTwo, bookThree, bookFour, bookFive;
    private User userOne, userTwo;

    @BeforeEach
    void setUp() {
        library = new Library();

        bookOne = new Book("The Lord of the Rings", "J.R.R. Tolkien", "Fantasy");
        bookTwo = new Book("Pride and Prejudice", "Jane Austen", "Romance");
        bookThree = new Book("1984", "George Orwell", "Dystopian");
        bookFour = new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy");
        bookFive = new Book("To Kill a Mockingbird", "Harper Lee", "Fiction");

        library.addBook(bookOne);
        library.addBook(bookTwo);
        library.addBook(bookThree);
        library.addBook(bookFour);
        library.addBook(bookFive);

        userOne = new User("Alice");
        userTwo = new User("Bob");

        library.addUser(userOne);
        library.addUser(userTwo);
    }

    @Test
    @DisplayName("Should successfully borrow a book")
    void shouldSuccessfullyBorrowBook() {
        assertDoesNotThrow(() -> library.borrowBook(userOne.getId(), bookOne.getId()));
        assertTrue(bookOne.isBorrowed());
        assertTrue(userOne.getBorrowedBooks().contains(bookOne));
        assertEquals(1, userOne.getNumberOfBorrowedBooks());
        assertEquals(4, library.getAvailableBooks().size());
        assertEquals(1, library.getBorrowedBooks().size());
    }

    @Test
    @DisplayName("Should throw BookUnavailableException if book is already borrowed")
    void shouldThrowBookUnavailableExceptionIfBookAlreadyBorrowed() {
        library.borrowBook(userOne.getId(), bookOne.getId()); // User1 borrows book1

        assertThrows(BookUnavailableException.class, () -> library.borrowBook(userTwo.getId(), bookOne.getId()));
        assertTrue(bookOne.isBorrowed()); // Still borrowed by user1
        assertFalse(userTwo.getBorrowedBooks().contains(bookOne)); // User2 did not get it
    }

    @Test
    @DisplayName("Should throw BookLimitExceededException if user exceeds limit")
    void shouldThrowBookLimitExceededExceptionIfUserExceedsLimit() {
        // User1 borrows 3 books
        library.borrowBook(userOne.getId(), bookOne.getId());
        library.borrowBook(userOne.getId(), bookTwo.getId());
        library.borrowBook(userOne.getId(), bookThree.getId());

        assertEquals(3, userOne.getNumberOfBorrowedBooks());

        // User1 tries to borrow a 4th book
        assertThrows(BookLimitExceededException.class, () -> library.borrowBook(userOne.getId(), bookFour.getId()));
        assertFalse(bookFour.isBorrowed()); // Book4 should not be borrowed
        assertFalse(userOne.getBorrowedBooks().contains(bookFour));
        assertEquals(3, userOne.getNumberOfBorrowedBooks()); // Still 3 books
    }

    @Test
    @DisplayName("Should successfully return a borrowed book")
    void shouldSuccessfullyReturnBook() {
        library.borrowBook(userOne.getId(), bookOne.getId());
        assertTrue(bookOne.isBorrowed());
        assertEquals(1, userOne.getNumberOfBorrowedBooks());

        assertDoesNotThrow(() -> library.returnBook(userOne.getId(), bookOne.getId()));
        assertFalse(bookOne.isBorrowed());
        assertFalse(userOne.getBorrowedBooks().contains(bookOne));
        assertEquals(0, userOne.getNumberOfBorrowedBooks());
        assertEquals(5, library.getAvailableBooks().size());
        assertEquals(0, library.getBorrowedBooks().size());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if returning a book not borrowed by user")
    void shouldThrowIllegalArgumentExceptionIfReturningBookNotBorrowedByUser() {
        library.borrowBook(userOne.getId(), bookOne.getId()); // User1 borrows book1

        // User2 tries to return book1 (which they didn't borrow)
        assertThrows(IllegalArgumentException.class, () -> library.returnBook(userTwo.getId(), bookOne.getId()));
        assertTrue(bookOne.isBorrowed()); // Still borrowed by user1
        assertTrue(userOne.getBorrowedBooks().contains(bookOne));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if returning an unborrowed book")
    void shouldThrowIllegalArgumentExceptionIfReturningUnborrowedBook() {
        // book2 is not borrowed by anyone
        assertFalse(bookTwo.isBorrowed());
        assertThrows(IllegalArgumentException.class, () -> library.returnBook(userOne.getId(), bookTwo.getId()));
        assertFalse(bookTwo.isBorrowed());
    }

    @Test
    @DisplayName("Should list all available books correctly")
    void shouldListAvailableBooksCorrectly() {
        library.borrowBook(userOne.getId(), bookOne.getId());
        library.borrowBook(userTwo.getId(), bookTwo.getId());

        List<Book> availableBooks = library.getAvailableBooks();
        assertEquals(3, availableBooks.size()); // 5 total - 2 borrowed = 3 available
        assertFalse(availableBooks.contains(bookOne));
        assertFalse(availableBooks.contains(bookTwo));
        assertTrue(availableBooks.contains(bookThree));
        assertTrue(availableBooks.contains(bookFour));
        assertTrue(availableBooks.contains(bookFive));
    }

    @Test
    @DisplayName("Should list all borrowed books correctly")
    void shouldListBorrowedBooksCorrectly() {
        library.borrowBook(userOne.getId(), bookOne.getId());
        library.borrowBook(userTwo.getId(), bookTwo.getId());

        List<Book> borrowedBooks = library.getBorrowedBooks();
        assertEquals(2, borrowedBooks.size());
        assertTrue(borrowedBooks.contains(bookOne));
        assertTrue(borrowedBooks.contains(bookTwo));
        assertFalse(borrowedBooks.contains(bookThree));
    }

    @Test
    @DisplayName("Should filter books by author correctly")
    void shouldFilterBooksByAuthorCorrectly() {
        List<Book> tolkienBooks = library.findBooksByAuthor("Tolkien");
        assertEquals(2, tolkienBooks.size());
        assertTrue(tolkienBooks.contains(bookOne)); // Lord of the Rings
        assertTrue(tolkienBooks.contains(bookFour)); // The Hobbit

        List<Book> austenBooks = library.findBooksByAuthor("Jane Austen");
        assertEquals(1, austenBooks.size());
        assertTrue(austenBooks.contains(bookTwo));
    }

    @Test
    @DisplayName("Should filter books by genre correctly")
    void shouldFilterBooksByGenreCorrectly() {
        List<Book> fantasyBooks = library.findBooksByGenre("Fantasy");
        assertEquals(2, fantasyBooks.size());
        assertTrue(fantasyBooks.contains(bookOne));
        assertTrue(fantasyBooks.contains(bookFour));

        List<Book> romanceBooks = library.findBooksByGenre("Romance");
        assertEquals(1, romanceBooks.size());
        assertTrue(romanceBooks.contains(bookTwo));
    }

    @Test
    @DisplayName("Should return Optional.of(Book) if book found by ID")
    void shouldReturnOptionalOfBookIfBookFoundById() {
        Optional<Book> foundBook = library.findBookById(bookThree.getId());
        assertTrue(foundBook.isPresent());
        assertEquals(bookThree, foundBook.get());
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
        assertThrows(IllegalArgumentException.class, () -> library.borrowBook("non-existent-user", bookOne.getId()));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if book not found during borrow")
    void shouldThrowIllegalArgumentExceptionIfBookNotFoundBorrow() {
        assertThrows(IllegalArgumentException.class, () -> library.borrowBook(userOne.getId(), "non-existent-book"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if user not found during return")
    void shouldThrowIllegalArgumentExceptionIfUserNotFoundReturn() {
        library.borrowBook(userOne.getId(), bookOne.getId());
        assertThrows(IllegalArgumentException.class, () -> library.returnBook("non-existent-user", bookOne.getId()));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if book not found during return")
    void shouldThrowIllegalArgumentExceptionIfBookNotFoundReturn() {
        library.borrowBook(userOne.getId(), bookOne.getId());
        assertThrows(IllegalArgumentException.class, () -> library.returnBook(userOne.getId(), "non-existent-book"));
    }
}
