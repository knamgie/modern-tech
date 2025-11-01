package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Model {
    public abstract String getDescribeMessage();

    public abstract void showCommands();

    public String readCommand() {
        return IO.readln("\nВведите номер команды: ");
    }

    public abstract void runCommandWithConnection(String command, Connection connection);

    protected void showTables(Connection connection) throws RuntimeException {
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

    protected abstract void createTable(Connection connection);

    protected abstract void saveToExcel(Connection connection);
}
