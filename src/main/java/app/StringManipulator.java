package app;

import control.Control;
import model.TwoStrings;

public class StringManipulator {
    static void main() {
        try {
            Control<TwoStrings> control = new Control<>(new TwoStrings());
            control.connectToLocalDb();
            control.handleCommands();
        } catch (RuntimeException e) {
            System.err.println("Ошибка.");
            System.err.println("Сообщение об ошибке: " + e.getMessage());
        }
    }
}
