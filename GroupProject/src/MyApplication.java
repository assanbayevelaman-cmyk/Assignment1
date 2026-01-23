import Controllers.BooksController;
import java.util.InputMismatchException;
import java.util.Scanner;
public class MyApplication {
    private final BooksController controller;
    private final Scanner scanner;
    public MyApplication(BooksController controller) {
        this.controller = controller;
        scanner = new Scanner(System.in);
    }
    public void start() {
        while (true) {
            System.out.println();
            System.out.println("Welcome to My Application");
            System.out.println("Select option:");
            System.out.println("1. Get all books");
            System.out.println("2. Get books by id");
            System.out.println("3. Create books");
            System.out.println("0. Exit");
            System.out.println();
            try {
                System.out.print("Enter option (1-3): ");
                int option = scanner.nextInt();
                if (option == 1) {
                    getAllBooksMenu();
                } else if (option == 2) {
                    getBooksByIdMenu();
                } else if (option == 3) {
                    createBooksMenu();
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Input must be an integer");
                scanner.nextLine();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println("*************************");
        }
    }
    public void getAllBooksMenu() {
        String response = controller.getAllBooks();
        System.out.println(response);
    }
    public void getBooksByIdMenu() {
        System.out.println("Please enter id");
        int id = scanner.nextInt();
        String response = controller.getBooks(id);
        System.out.println(response);
    }
    public void createBooksMenu() {
        System.out.println("Please enter title");
        String title = scanner.next();
        System.out.println("Please enter author");
        String author = scanner.next();
        System.out.println("Please enter publication year");
        String publication_year = scanner.next();
        String response = controller.createBooks(title, author, Integer.parseInt(publication_year));
        System.out.println(response);
    }
}
