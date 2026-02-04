package org.example.repositories.interfaces;

import org.example.entities.Book;
import java.util.List;
import java.util.function.Predicate;
import java.util.Comparator;

public interface IBookRepository {
    boolean createBook(Book book);
    Book getBook(int id);
    List<Book> getAllBooks();
    boolean updateBookQuantity(int id, int newQuantity);
    boolean updateBook(Book book);
    List<Book> getBooksByCategory(int categoryId);
    List<Book> getAllBooksWithCategory();
    List<Book> getBooksByAuthor(String author);
    List<Book> searchBooksByTitle(String title);
    List<Book> searchBooksByAuthor(String author);
    boolean bookExists(String title, String author);
    int getTotalBookCount();
    int getBookCountByCategory(int categoryId);
    List<Book> filterBooks(Predicate<Book> predicate);
    List<Book> sortBooks(Comparator<Book> comparator);
}