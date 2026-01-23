import Controllers.BooksController;
import Database.BookstoreDB;
import Database.Interface.IDB;
import Repositories.BooksRepository;
import Repositories.Interface.IBooksRepository;
import java.sql.*;
public class Main {
    public static void main(String[] args) {
        IDB db = new BookstoreDB();
        IBooksRepository repo = new BooksRepository(db);
        BooksController controller = new BooksController(repo);
        MyApplication app = new MyApplication(controller);
        app.start();
    }
}
