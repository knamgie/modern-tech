package control;

import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Control {
    private Connection connection_ = null;
    // private Model<T> model = null;

    public Control() throws RuntimeException {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            System.err.println("Невозможно зарегистрировать драйвер.");
            throw new RuntimeException(e);
        }

        // model = new Arithmetic();

        IO.println("Драйвер успешно зарегистрирован.");
        IO.println("Ваша модель: " + model.greetString);
    }

    public void connectToLocalDb() {
        while (connection_ == null) {
            String dbName = IO.readln("\nВведите название локальной схемы (БД) из MySQL: ");
            String dbUrl = "jdbc:mysql://localhost/" + dbName;
            String username = IO.readln("Имя пользователя: ");
            String password = IO.readln("Пароль: ");

            try {
                connection_ = DriverManager.getConnection(dbUrl, username, password);
            } catch (SQLException e) {
                IO.println("\nОшибка входа. Попробуйте еще раз.");
            }
        }

        IO.println("\nВы успешно подключились к БД.");
    }

    public void handleCommands() {
        do {
            model.showCommands();
            model.runCommand(model.readCommand());
        } while (wantContinue());
    }

    private boolean wantContinue() {
        while (true) {
            String answer = IO.readln("\nПродолжить? Y/n: ");
            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) return true;
            if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) return false;
        }
    }
}
