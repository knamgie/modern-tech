package model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Objects;

public class TwoStrings extends Model {
    private final ArrayList<String> acceptedStrings = new ArrayList<>();

    @Override
    public String getDescribeMessage() {
        return "`Модель Двух Строк`";
    }

    @Override
    public void showCommands() {
        IO.println("\nДоступные команды:");
        IO.println("1. Вывести таблицы из MySQL.");
        IO.println("2. Создать таблицу в MySQL.");
        IO.println("3. Ввести две строки с клавиатуры, результат сохранить в MySQL.");
        IO.println("4. Подсчитать размер ранее введенных строк, результат сохранить в MySQL.");
        IO.println("5. Объединить две строки в единое целое, результат сохранить в MySQL.");
        IO.println("6. Сравнить две ранее введенные строки, результат сохранить в MySQL.");
        IO.println("7. Сохранить все полученные данные из MySQL в Excel и вывести на экран.");
    }

    @Override
    public void runCommandWithConnection(String command, Connection connection)
        throws RuntimeException
    {
        switch (command) {
            case "1" -> showTables(connection);
            case "2" -> createTable(connection, "TEXT");
            case "3" -> acceptTwoStrings(connection);
            case "4" -> calculateTotalLength(connection);
            case "5" -> concatStrings(connection);
            case "6" -> compareStrings(connection);
            case "7" -> saveToExcel(connection);
            default -> IO.println("Невалидный номер команды.");
        }
    }

    private void acceptTwoStrings(Connection connection) throws RuntimeException {
        String first = IO.readln("\nВведите первую строку: ");
        String second = IO.readln("Введите вторую строку: ");

        acceptedStrings.add(first);
        acceptedStrings.add(second);

        IO.println("\nСтрока 1: " + first + "; Строка 2: " + second);

        finishQuery(connection, first, "Строка 1: " + first);
        finishQuery(connection, second, "Строка 2: " + second);
    }

    private void calculateTotalLength(Connection connection) throws RuntimeException {
        int totalLength = acceptedStrings.stream().mapToInt(String::length).sum();

        IO.println("\nРазмер ранее введенных строк: " + totalLength);

        finishQuery(
            connection, Integer.toString(totalLength), "Размер строк: " + totalLength
        );
    }

    private void concatStrings(Connection connection) throws RuntimeException {
        String first = IO.readln("\nВведите первую строку: ");
        String second = IO.readln("Введите вторую строку: ");
        String result = first + second;

        acceptedStrings.add(result);

        IO.println("\nОбъединенная строка: " + result);

        finishQuery(connection, result, first + " + " + second);
    }

    private void compareStrings(Connection connection) throws RuntimeException {
        if (acceptedStrings.isEmpty()) {
            IO.println("Нет раннее введенных строк, попробуйте сделать другой запрос.");
            return;
        }

        String first = acceptedStrings.getLast();
        String second = acceptedStrings.get(acceptedStrings.size() - 2);

        String result;
        String query;
        if (Objects.equals(first, second)) {
            result = "true";
            query = "Строки `" + first + "` и `" + second + "` равны";
        } else {
            result = "false";
            query = "Строки `" + first + "` и `" + second + "` не равны";
        }

        IO.println(query);

        finishQuery(connection, result, query);
    }
}
