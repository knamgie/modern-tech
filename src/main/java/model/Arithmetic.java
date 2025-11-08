package model;

import java.sql.*;

public class Arithmetic extends Model {
    @Override
    public String getDescribeMessage() {
        return "`Модель Калькулятора`";
    }

    @Override
    public void showCommands() {
        IO.println("\nДоступные команды:");
        IO.println("1. Вывести таблицы из MySQL.");
        IO.println("2. Создать таблицу в MySQL.");
        IO.println("3. Сложение чисел, результат сохранить в MySQL.");
        IO.println("4. Вычитание чисел, результат сохранить в MySQL.");
        IO.println("5. Умножение чисел, результат сохранить в MySQL.");
        IO.println("6. Деление чисел, результат сохранить в MySQL.");
        IO.println("7. Взятие числа по модулю, результат сохранить в MySQL.");
        IO.println("8. Возведение числа в степень, результат сохранить в MySQL.");
        IO.println("9. Сохранить все полученные данные из MySQL в Excel и вывести на экран.");
    }

    @Override
    public void runCommandWithConnection(String command, Connection connection)
        throws RuntimeException
    {
        switch (command) {
            case "1" -> showTables(connection);
            case "2" -> createTable(connection, "varchar(255)");
            case "3" -> sumNumbers(connection);
            case "4" -> subtractNumbers(connection);
            case "5" -> multiplyNumbers(connection);
            case "6" -> divideNumbers(connection);
            case "7" -> modNumbers(connection);
            case "8" -> powerNumbers(connection);
            case "9" -> saveToExcel(connection);
            default -> IO.println("Невалидный номер команды.");
        }
    }

    private void sumNumbers(Connection connection) throws RuntimeException {
        try {
            Double lhs = Double.parseDouble(IO.readln("\nВведите первое слагаемое: "));
            Double rhs = Double.parseDouble(IO.readln("Введите второе слагаемое: "));
            String result = Double.toString(lhs + rhs);

            IO.println("\nСумма: " + lhs + " + " + rhs + " = " + result);

            finishQuery(connection, result, lhs + " + " + rhs + " = " + result);
        } catch (NumberFormatException e) {
            IO.println("Неверный формат.");
        }
    }

    private void subtractNumbers(Connection connection) throws RuntimeException {
        try {
            Double lhs = Double.parseDouble(IO.readln("\nВведите уменьшаемое: "));
            Double rhs = Double.parseDouble(IO.readln("Введите вычитаемое: "));
            String result = Double.toString(lhs - rhs);

            IO.println("\nРазность: " + lhs + " - " + rhs + " = " + result);

            finishQuery(connection, result, lhs + " - " + rhs + " = " + result);
        } catch (NumberFormatException e) {
            IO.println("Неверный формат.");
        }
    }

    private void multiplyNumbers(Connection connection) throws RuntimeException {
        try {
            Double lhs = Double.parseDouble(IO.readln("\nВведите первое слагаемое: "));
            Double rhs = Double.parseDouble(IO.readln("Введите второе слагаемое: "));
            String result = Double.toString(lhs * rhs);

            IO.println("\nУмножение: " + lhs + " * " + rhs + " = " + result);

            finishQuery(connection, result, lhs + " * " + rhs + " = " + result);
        } catch (NumberFormatException e) {
            IO.println("Неверный формат.");
        }
    }

    private void divideNumbers(Connection connection) throws RuntimeException {
        try {
            Double lhs = Double.parseDouble(IO.readln("\nВведите делимое: "));
            Double rhs = Double.parseDouble(IO.readln("Введите делитель: "));
            String result = Double.toString(lhs / rhs);

            IO.println("\nЧастное: " + lhs + " / " + rhs + " = " + result);

            finishQuery(connection, result, lhs + " / " + rhs + " = " + result);
        } catch (NumberFormatException e) {
            IO.println("Неверный формат.");
        }
    }

    private void modNumbers(Connection connection) throws RuntimeException {
        try {
            Integer number = Integer.parseInt(IO.readln("\nВведите число: "));
            Integer mod = Integer.parseInt(IO.readln("Введите модуль: "));
            String result = Integer.toString(number % mod);

            IO.println("\nМодуль: " + number + " % " + mod + " = " + result);

            finishQuery(connection, result, number + " % " + mod + " = " + result);
        } catch (NumberFormatException e) {
            IO.println("Неверный формат.");
        }
    }

    private void powerNumbers(Connection connection) throws RuntimeException {
        try {
            double number = Double.parseDouble(IO.readln("\nВведите число: "));
            double power = Double.parseDouble(IO.readln("Введите степень: "));
            String result = Double.toString(Math.pow(number, power));

            IO.println("\nВозведение в степень: " + number + " ^ " + power + " = " + result);

            finishQuery(connection, result, number + " ^ " + power + " = " + result);
        } catch (NumberFormatException e) {
            IO.println("Неверный формат.");
        }
    }
}
