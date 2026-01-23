package org.example;

import org.example.data.PostgresDB;
import org.example.repositories.BookRepository;
import org.example.repositories.OrderRepository;
import org.example.repositories.interfaces.IBookRepository;
import org.example.repositories.interfaces.IOrderRepository;
import controllers.BookController;

public class Main {
    public static void main(String[] args) {
        PostgresDB db = new PostgresDB();
        IBookRepository bookRepo = new BookRepository(db);
        IOrderRepository orderRepo = new OrderRepository(db);
        BookController controller = new BookController(bookRepo, orderRepo);
        MyApplication app = new MyApplication(controller);

        app.start();
    }
}