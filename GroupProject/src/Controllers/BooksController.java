package Controllers;
import Entities.Books;
import Repositories.Interface.IBooksRepository;
import java.util.List;

public class BooksController {
    private final IBooksRepository repo;
    public BooksController(IBooksRepository repo) {
        this.repo = repo;
    }
    public String createBooks(String title, String author, int publication_year) {
        Books books = new Books(title, author, publication_year);
        boolean created = repo.createBooks(books);
        return (created ? "Books was created!" : "Books creation was failed!");
    }
    public String getBooks(int id) {
        Books books = repo.getBooks(id);
        return (books == null ? "Books was not found!" : books.toString());
    }
    public String getAllBooks() {
        List<Books> books = repo.getAllBooks();
        return books.toString();
    }
}
