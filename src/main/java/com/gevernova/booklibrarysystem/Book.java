package com.gevernova.booklibrarysystem;
import java.util.Objects;
import java.util.UUID;

public class Book {
    private final String id;
    private String title;
    private String author;
    private String genre;
    private boolean isBorrowed;

    public Book(String title, String author, String genre) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Book title cannot be null or blank.");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Book author cannot be null or blank.");
        }
        if (genre == null || genre.isBlank()) {
            throw new IllegalArgumentException("Book genre cannot be null or blank.");
        }
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isBorrowed = false;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id.substring(0, 8) + '\'' + // Shorten ID for readability
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", isBorrowed=" + isBorrowed +
                '}';
    }
}
