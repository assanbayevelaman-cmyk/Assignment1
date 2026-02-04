package org.example.repositories.interfaces;

import org.example.entities.Book;
import java.util.List;

public interface IBookRepository {
    boolean createBook(Book book);
    Book getBook(int id);
    List<Book> getAllBooks();
    boolean updateBookQuantity(int id, int newQuantity);
    boolean updateBook(Book book);
}