package org.example;

import controllers.BookController;
import java.util.Scanner;

public class MyApplication {
    private final BookController controller;
    private final Scanner scanner;

    public MyApplication(BookController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Online Bookstore ===");
            System.out.println("1. Get all books\n2. Get book by id\n3. Create book\n4. Buy book\n5. View all orders\n6. Cancel order\n0. Exit\nChoose option: ");

            try {
                int option = scanner.nextInt();
                if (option == 0) {
                    System.out.println("Goodbye!");
                    scanner.close();
                    break;
                }
                switch(option) {
                    case 1 -> System.out.println(controller.getAllBooks());
                    case 2 -> {
                        System.out.print("Enter book id: ");
                        System.out.println(controller.getBook(scanner.nextInt()));
                    }
                    case 3 -> createBookMenu();
                    case 4 -> buyBookMenu();
                    case 5 -> System.out.println(controller.getAllOrders());
                    case 6 -> {
                        System.out.print("Enter order id to cancel: ");
                        System.out.println(controller.cancelOrder(scanner.nextInt()));
                    }
                    default -> System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("Error!");
                scanner.nextLine();
            }
        }
    }

    private void createBookMenu() {
        scanner.nextLine();
        System.out.print("Title: "); String t = scanner.nextLine();
        System.out.print("Author: "); String a = scanner.nextLine();
        System.out.print("Price: "); double p = scanner.nextDouble();
        System.out.print("Quantity: "); int q = scanner.nextInt();
        System.out.println(controller.createBook(t, a, p, q));
    }

    private void buyBookMenu() {
        scanner.nextLine();
        System.out.print("Customer name: "); String customer = scanner.nextLine();
        System.out.print("ID: "); int id = scanner.nextInt();
        System.out.print("Quantity: "); int qty = scanner.nextInt();
        System.out.println(controller.buyBook(id, qty, customer));
    }
}