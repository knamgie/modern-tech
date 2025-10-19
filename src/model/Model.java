package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Model {
    public String getDescribeMessage() {
        return "`Стандартная Модель`";
    }

    public abstract void showCommands();

    public String readCommand() {
        return IO.readln("\nВведите номер команды: ");
    }

    public abstract void runCommandWithConnection(String command, Connection connection);

    public void showTables(Connection connection) throws RuntimeException {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SHOW TABLES");

            IO.println("\nДоступные таблицы:");
            iterateShowTablesResultSet(resultSet);
        } catch (SQLException e) {
            System.err.println("Невозможно выполнить запрос: `SHOW TABLES`.");
            throw new RuntimeException(e);
        }
    }

    private void iterateShowTablesResultSet(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            IO.println("- " + resultSet.getString(1)); // `1`: Table name.
        }
    }
}

