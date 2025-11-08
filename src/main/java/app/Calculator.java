package app;

import control.Control;
import model.Arithmetic;

public class Calculator {
    static void main() {
        try {
            Control<Arithmetic> control = new Control<>(new Arithmetic());
            control.connectToLocalDb();
            control.handleCommands();
        } catch (RuntimeException e) {
            System.err.println("Ошибка.");
            System.err.println("Сообщение об ошибке: " + e.getMessage());
        }
    }
}