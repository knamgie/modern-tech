package model;

public abstract class Model {
    public String getDescribeMessage() {
        return "`Стандартная Модель`";
    }

    public abstract void showCommands();

    public String readCommand() {
        return IO.readln("\nВведите номер команды: ");
    }

    public abstract void runCommand(String command);
}
