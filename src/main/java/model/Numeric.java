package model;

import java.sql.Connection;

public class Numeric extends Model {
    @Override
    public String getDescribeMessage() {
        return "`Модель Определителя Чисел`";
    }

    @Override
    public void showCommands() {
        IO.println("\nДоступные команды:");
        IO.println("1. Вывести таблицы из MySQL.");
        IO.println("2. Создать таблицу в MySQL.");
        IO.println("3. Проверить число на целостность и четность, результат сохранить в MySQL.");
        IO.println("4. Сохранить все полученные данные из MySQL в Excel и вывести на экран.");
    }

    @Override
    public void runCommandWithConnection(String command, Connection connection)
        throws RuntimeException
    {
        switch (command) {
            case "1" -> showTables(connection);
            case "2" -> createTable(connection, "varchar(255)");
            case "3" -> validateNumber(connection);
            case "4" -> saveToExcel(connection);
            default -> IO.println("Невалидный номер команды.");
        }
    }

    private void validateNumber(Connection connection) throws RuntimeException {
        try {
            int number = Integer.parseInt(IO.readln("\nВведите число: "));

            String result;
            if (number % 2 == 0) {
                result = "even";
                IO.println("\nВаше число `" + number + "` четное.");
            } else {
                result = "odd";
                IO.println("\nВаше число `" + number + "` нечетное.");
            }

            finishQuery(connection, result, number + " -> " + result);
        } catch (NumberFormatException e) {
            IO.println("Неверный формат. Ошибка!");
        }
    }
}
