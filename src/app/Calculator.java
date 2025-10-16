package app;

import control.Control;

public class Calculator {
    static void main() {
        try {
            Control control = new Control();
            control.connectToLocalDb();
            control.handleCommands();
        } catch (RuntimeException e) {
            System.err.println("Ошибка.");
            System.err.println("Сообщение об ошибке: " + e.getMessage());
        }
    }
}
