package model;

public class Arithmetic extends Model{
    @Override
    public String getDescribeMessage() {
        return "`Модель Калькулятора`";
    }

    @Override
    public void showCommands() {
        IO.println("\nДоступные команды:");
        IO.println("1. Вывести все таблицы из MySQL.");
        IO.println("2. Создать таблицу в MySQL.");
        IO.println("3. Сложение чисел, результат сохранить в MySQL.");
        IO.println("4. Вычитание чисел, результат сохранить в MySQL.");
        IO.println("5. Умножение чисел, результат сохранить в MySQL.");
        IO.println("6. Деление чисел, результат сохранить в MySQL.");
        IO.println("7. Деление чисел по модулю, результат сохранить в MySQL.");
        IO.println("8. Возведение числа в модуль, результат сохранить в MySQL.");
        IO.println("9. Возведение числа в степень, результат сохранить в MySQL.");
        IO.println("10. Сохранить все полученные данные из MySQL в Excel и вывести на экран.");
    }

    @Override
    public void runCommand(String command) {
        switch (command) {
            case "1" -> showTables();
            default -> IO.println("Невалидный номер команды.");
        }
    }

    private void showTables() {
        IO.println("\nTables.");
    }
}
