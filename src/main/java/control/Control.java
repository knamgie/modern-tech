package control;

import com.mysql.cj.jdbc.Driver;
import model.Model;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Control<E extends Model> {
    private Connection connection_ = null;
    private final E model_;

    public Control(E model) throws RuntimeException {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

            DriverManager.registerDriver(new Driver());
        } catch (Exception e) {
            System.err.println("Невозможно зарегистрировать драйвер.");
            throw new RuntimeException(e);
        }

        model_ = model;

        IO.println("Драйвер успешно зарегистрирован.");
        IO.println("Ваша модель: " + model.getDescribeMessage());
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

    public void handleCommands() throws RuntimeException {
        do {
            model_.showCommands();
            model_.runCommandWithConnection(model_.readCommand(), connection_);
        } while (needContinue());
    }

    private boolean needContinue() {
        while (true) {
            String answer = IO.readln("\nПродолжить? Y/n: ");

            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                return true;
            }
            if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
                return false;
            }
        }
    }
}
