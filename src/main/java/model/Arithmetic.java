package model;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import query.SavedQuery;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;

public class Arithmetic extends Model {
    private final ArrayList<SavedQuery> savedQueries_ = new ArrayList<>();

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
            case "2" -> createTable(connection);
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

    @Override
    protected void createTable(Connection connection) throws RuntimeException {
        String tableName = IO.readln("\nВведите название новой таблицы: ");
        String query = "CREATE TABLE IF NOT EXISTS " + tableName
            + "(id int AUTO_INCREMENT PRIMARY KEY, result varchar(255))";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Невозможно создать таблицу.");
            throw new RuntimeException(e);
        }

        IO.println("Таблица создана.");
    }

    private String chooseTableToSave(Connection connection) {
        ArrayList<String> possibleTablesToSave = findCorrectTables(connection);

        if (possibleTablesToSave.isEmpty()) {
            IO.println("Нет доступных таблиц для сохранения.");
            return null;
        }

        IO.println("\nВыберите таблицу для сохранения результата:");

        for (int i = 0; i < possibleTablesToSave.size(); ++i) {
            IO.println((i + 1) + ". " + possibleTablesToSave.get(i));
        }

        int tableNum = Integer.parseInt(IO.readln("\nВведите номер таблицы: ")) - 1;

        return possibleTablesToSave.get(tableNum);
    }

    private ArrayList<String> findCorrectTables(Connection connection) {
        ArrayList<String> correctTables = new ArrayList<>();

        try {
            DatabaseMetaData metaData = connection.getMetaData();

            try (
                ResultSet tables =
                    metaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"})
            ) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");

                    if (isTableCorrect(metaData, tableName)) {
                        correctTables.add(tableName);
                    }
                }
            }

            return correctTables;
        } catch (SQLException e) {
            System.err.println("Невозможен поиск подходящих таблиц.");
            throw new RuntimeException(e);
        }
    }

    private boolean isTableCorrect(DatabaseMetaData metaData, String tableName) {
        return hasCorrectPrimaryKey(metaData, tableName)
            && hasCorrectResultColumn(metaData, tableName);
    }

    private boolean hasCorrectPrimaryKey(DatabaseMetaData metaData, String tableName) {
        try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName)) {
            boolean answer = false;

            if (primaryKeys.next()) {
                String pkColumn = primaryKeys.getString("COLUMN_NAME");
                ResultSet columns = metaData.getColumns(null, null, tableName, pkColumn);
                if (columns.next()) {
                    answer = "YES".equals(columns.getString("IS_AUTOINCREMENT"));
                }
            }

            return answer;
        } catch (SQLException e) {
            System.err.println("Невозможно получить первичные ключи.");
            throw new RuntimeException(e);
        }
    }

    private boolean hasCorrectResultColumn(DatabaseMetaData metaData, String tableName) {
        try (ResultSet columns = metaData.getColumns(null, null, tableName, "result")) {
            boolean answer = false;

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");
                String isNullable = columns.getString("IS_NULLABLE");
                String columnDefault = columns.getString("COLUMN_DEF");

                boolean isStringType = typeName.toUpperCase().contains("CHAR")
                    || typeName.toUpperCase().contains("TEXT");

                boolean isPrimaryKey = isPrimaryKeyColumn(metaData, tableName, columnName);

                if (isStringType && !isPrimaryKey) {
                    if ("YES".equals(isNullable) || columnDefault != null) {
                        answer = true;
                        break;
                    }
                }
            }

            return answer;
        } catch (SQLException e) {
            System.err.println("Невозможно получить нужные столбцы.");
            throw new RuntimeException(e);
        }
    }

    private boolean isPrimaryKeyColumn(
        DatabaseMetaData metaData, String tableName, String columnName
    ) {
        try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName)) {
            while (primaryKeys.next()) {
                if (columnName.equals(primaryKeys.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    private String saveToTable(Connection connection, String tableToSave, String result) {
        String query = "INSERT INTO " + tableToSave + " (result) VALUES (" + result + ")";

        try (
            PreparedStatement statement =
                connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException();
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getString(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Не удалось сохранить значение в таблицу.");
        }

        return null;
    }

    private void finishQuery(Connection connection, String result, String query) {
        String tableToSave = chooseTableToSave(connection);

        if (tableToSave == null) {
            return;
        }

        String id = saveToTable(connection, tableToSave, result);

        savedQueries_.add(new SavedQuery(id, query, tableToSave));

        IO.println("\nЗначение сохранено.");
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

    private void checkQueries() {
        if (savedQueries_.isEmpty()) {
            IO.println("\nНет данных, полученных в ходе данной сессии.");
        } else {
            IO.println("\nДанные, полученные в ходе текущей сессии:");
            for (SavedQuery query : savedQueries_) {
                query.showInfo();
            }
        }
    }

    @Override
    protected void saveToExcel(Connection connection) {
        checkQueries();

        String tableName =
            IO.readln("\nВведите название таблицы, которую вы хотите сохранить в Excel: ");

        try (
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            Workbook workbook = new XSSFWorkbook();
            FileOutputStream fos = new FileOutputStream("build/" + tableName + ".xlsx")
        ) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            Sheet sheet = workbook.createSheet(tableName);
            Row headerRow = sheet.createRow(0);

            for (int i = 1; i <= columnCount; i++) {
                headerRow.createCell(i - 1).setCellValue(metaData.getColumnName(i));
            }

            int rowNum = 1;
            while (resultSet.next()) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    if (value != null) {
                        row.createCell(i - 1).setCellValue(value.toString());
                    }
                }
            }

            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos);

            System.out.println("Таблица экспортирована.");
        } catch (Exception e) {
            System.err.println(
                "Невозможно сохранить результат в Excel. Может быть такой таблицы нет."
            );
        }
    }
}
