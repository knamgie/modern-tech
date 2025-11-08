package app;

import control.Control;
import model.StringMethods;

public class StringExplorer {
    static void main() {
        try {
            Control<StringMethods> control = new Control<>(new StringMethods());
            control.connectToLocalDb();
            control.handleCommands();
        } catch (RuntimeException e) {
            System.err.println("Ошибка.");
            System.err.println("Сообщение об ошибке: " + e.getMessage());
        }
    }
}
