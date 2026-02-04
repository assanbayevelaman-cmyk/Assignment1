package org.example;

public class Main {
    public static void main(String[] args) {
        try {
            MyApplication app = new MyApplication();
            app.start();

        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }
}