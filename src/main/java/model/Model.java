package model;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import query.SavedQuery;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;

public abstract class Model {
    protected final ArrayList<SavedQuery> savedQueries_ = new ArrayList<>();

    public abstract String getDescribeMessage();
    public abstract void showCommands();

    public String readCommand() {
        return IO.readln("\nВведите номер команды: ");
    }

    public abstract void runCommandWithConnection(String command, Connection connection);

    void showTables(Connection connection) throws RuntimeException {
        try (ResultSet resultSet = connection.createStatement().executeQuery("SHOW TABLES")) {
            IO.println("\nДоступные таблицы:");

            while (resultSet.next()) {
                IO.println("- " + resultSet.getString(1)); // `1`: Table name.
            }
        } catch (SQLException e) {
            System.err.println("Невозможно выполнить запрос: `SHOW TABLES`.");
            throw new RuntimeException(e);
        }
    }

    void createTable(Connection connection, String resultType) {
        String tableName = IO.readln("\nВведите название новой таблицы: ");
        String query = "CREATE TABLE IF NOT EXISTS " + tableName
            + "(id int AUTO_INCREMENT PRIMARY KEY, result " + resultType + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Невозможно создать таблицу.");
            throw new RuntimeException(e);
        }

        IO.println("Таблица создана.");
    }

    private String chooseTableToSave(Connection connection, String result) throws RuntimeException {
        ArrayList<String> possibleTablesToSave = findCorrectTables(connection);

        if (possibleTablesToSave.isEmpty()) {
            IO.println("Нет доступных таблиц для сохранения.");
            return null;
        }

        IO.println("\nВыберите таблицу для сохранения результата `" + result + "`:");

        for (int i = 0; i < possibleTablesToSave.size(); ++i) {
            IO.println((i + 1) + ". " + possibleTablesToSave.get(i));
        }

        int tableNum = Integer.parseInt(IO.readln("\nВведите номер таблицы: ")) - 1;

        return possibleTablesToSave.get(tableNum);
    }

    private ArrayList<String> findCorrectTables(Connection connection) throws RuntimeException {
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

    private boolean isTableCorrect(DatabaseMetaData metaData, String tableName)
        throws RuntimeException
    {
        return hasCorrectPrimaryKey(metaData, tableName)
            && hasCorrectResultColumn(metaData, tableName);
    }

    private boolean hasCorrectPrimaryKey(DatabaseMetaData metaData, String tableName)
        throws RuntimeException
    {
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
        String query = "INSERT INTO " + tableToSave + " (result) VALUES (?)";

        try (
            PreparedStatement statement =
                connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, result);
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
            System.err.println("Сообщение: " + e.getMessage());
        }

        return null;
    }

    void finishQuery(Connection connection, String result, String query) throws RuntimeException {
        String tableToSave = chooseTableToSave(connection, result);

        if (tableToSave == null) {
            return;
        }

        String id = saveToTable(connection, tableToSave, result);

        savedQueries_.add(new SavedQuery(id, query, tableToSave));

        IO.println("\nЗначение сохранено.");
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

    void saveToExcel(Connection connection) {
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
