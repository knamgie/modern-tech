package model;

import java.sql.Connection;

public class StringMethods extends Model {
    @Override
    public String getDescribeMessage() {
        return "`Модель Методов Строк`"; // lul
    }

    @Override
    public void showCommands() {
        IO.println("\nДоступные команды:");
        IO.println("1. Вывести таблицы из MySQL.");
        IO.println("2. Создать таблицу в MySQL.");
        IO.println("3. Возвращение подстроки по индексам, результат сохранить в MySQL.");
        IO.println("4. Перевод строк в верхний и нижний регистры, результат сохранить в MySQL.");
        IO.println(
            "5. Поиск подстроки и определение окончания подстроки, результат сохранить в MySQL."
        );
        IO.println("6. Сохранить все полученные данные из MySQL в Excel и вывести на экран.");
    }

    @Override
    public void runCommandWithConnection(String command, Connection connection)
        throws RuntimeException {
        switch (command) {
            case "1" -> showTables(connection);
            case "2" -> createTable(connection, "TEXT");
            case "3" -> getSubstring(connection);
            case "4" -> changeStringCasing(connection);
            case "5" -> findSubstring(connection);
            case "6" -> saveToExcel(connection);
            default -> IO.println("Невалидный номер команды.");
        }
    }

    private void getSubstring(Connection connection) throws RuntimeException {
        try {
            String inputString = IO.readln("\nВведите строку: ");
            int beginIndex = Integer.parseInt(IO.readln("Введите нач. индекс подстроки: "));
            int endIndex = Integer.parseInt(IO.readln("Введите конеч. индекс подстроки: "));

            String result = inputString.substring(beginIndex, endIndex);

            IO.println("\nВычисленная подстрока из `" + inputString + "` - это `" + result + "`");

            finishQuery(
                connection, result,
                "`" + result + "` of `" + inputString + "` is from i=" + beginIndex + " to i="
                    + endIndex
            );
        } catch (IndexOutOfBoundsException e) {
            IO.println("Введены некорректные границы для индексов.");
        }
    }

    private void changeStringCasing(Connection connection) throws RuntimeException {
        String inputString = IO.readln("\nВведите строку: ");
        String inLowercase = inputString.toLowerCase();
        String inUppercase = inputString.toUpperCase();

        IO.println("\nСтрока в нижнем регистре: " + inLowercase);
        IO.println("Строка в верхнем регистре: " + inUppercase);

        finishQuery(
            connection, inLowercase,
            "lowercase of `" + inputString + "` is `" + inLowercase + "`"
        );
        finishQuery(
            connection, inLowercase,
            "uppercase of `" + inputString + "` is `" + inUppercase + "`"
        );
    }

    private void findSubstring(Connection connection) throws RuntimeException {
        String inputString = IO.readln("\nВведите строку: ");
        String inputSubstring = IO.readln("Введите подстроку для поиска: ");

        int indexResult = inputString.indexOf(inputSubstring);
        boolean doesEndWith = inputString.endsWith(inputSubstring);

        if (indexResult == -1) {
            IO.println("\nДанной подстроки в исходной строке нет.");

            finishQuery(
                connection, "false",
                "`" + inputString + "` doesn't include `" + inputSubstring + "`"
            );
        } else {
            IO.println("\nНачало первого вхождения искомой подстроки: " + indexResult);
            IO.println(
                "Заканчивается ли исходная строка данной подстрокой: " + doesEndWith
            );

            finishQuery(
                connection, Integer.toString(indexResult),
                "`" + inputSubstring + "` in `" + inputString + "` starts from position "
                    + indexResult
            );
            finishQuery(
                connection, Boolean.toString(doesEndWith),
                "does `" + inputString + "` ends with `" + inputSubstring + "`: " + doesEndWith
            );
        }
    }
}
