package org.example;

/**
 * Main Class.
 */
public class Main {
    /**
     * entry point of project.
     * @param args the cli arguments
     */
    public static void main(String[] args) {
        System.out.print("Hello and welcome!");

        writeOut();

    }

    public static void writeOut() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}
