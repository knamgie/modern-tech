package app;

import control.Control;
import model.Numeric;

public class NumbersValidator {
    static void main() {
        try {
            Control<Numeric> control = new Control<>(new Numeric());
            control.connectToLocalDb();
            control.handleCommands();
        } catch (RuntimeException e) {
            System.err.println("Ошибка.");
            System.err.println("Сообщение об ошибке: " + e.getMessage());
        }
    }
}
