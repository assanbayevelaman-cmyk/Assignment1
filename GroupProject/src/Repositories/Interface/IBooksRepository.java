package Repositories.Interface;
import Entities.Books;
import java.util.List;

public interface IBooksRepository {
    boolean createBooks(Books books);
    Books getBooks(int id);
    List<Books> getAllBooks();
}
