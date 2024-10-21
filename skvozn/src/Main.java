import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        // Укажи путь к входному и выходному файлу
        String inputFilePath = "input.html";
        String outputFilePath = "output.html";

        try {
            // Чтение HTML файла
            String content = new String(Files.readAllBytes(Paths.get(inputFilePath)));

            // Обработка содержимого файла: замена выражений результатами
            String processedContent = processContent(content);

            // Запись результата в выходной файл
            writeToFile(outputFilePath, processedContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для обработки содержимого файла
    public static String processContent(String content) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder processedContent = new StringBuilder();

        // Регулярное выражение для поиска арифметических выражений с неизвестными
        Pattern pattern = Pattern.compile("([a-zA-Z]+)\\s*([+\\-*/])\\s*([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(content);

        Map<String, Double> variables = new HashMap<>(); // Для хранения значений неизвестных
        int lastPos = 0;

        // Поиск всех арифметических выражений в тексте
        while (matcher.find()) {
            String var1 = matcher.group(1); // Первая переменная
            String operator = matcher.group(2); // Оператор (+, -, *, /)
            String var2 = matcher.group(3); // Вторая переменная

            // Получение значения для первой переменной, если она ещё не введена
            double value1 = variables.containsKey(var1) ? variables.get(var1) : getVariableValue(scanner, var1);
            variables.put(var1, value1); // Сохраняем переменную в Map

            // Получение значения для второй переменной, если она ещё не введена
            double value2 = variables.containsKey(var2) ? variables.get(var2) : getVariableValue(scanner, var2);
            variables.put(var2, value2); // Сохраняем переменную в Map

            double result = calculate(value1, operator, value2);

            // Добавляем к обработанному контенту результат вычисления
            processedContent.append(content, lastPos, matcher.start());
            processedContent.append(result); // Заменяем выражение результатом
            lastPos = matcher.end();
        }

        // Добавляем оставшуюся часть контента после последнего совпадения
        processedContent.append(content.substring(lastPos));

        return processedContent.toString();
    }

    public static double getVariableValue(Scanner scanner, String variable) {
        System.out.print("Введите значение для переменной " + variable + ": ");
        return scanner.nextDouble();
    }
    public static double calculate(double left, String operator, double right) {
        switch (operator) {
            case "+":
                return left + right;
            case "-":
                return left - right;
            case "*":
                return left * right;
            case "/":
                if (right == 0) {
                    System.out.println("Ошибка: деление на ноль!");
                    return 0;
                }
                return left / right;
            default:
                throw new IllegalArgumentException("Неподдерживаемая операция: " + operator);
        }
    }

    // Метод для записи данных в файл
    public static void writeToFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content); // Запись обработанного содержимого в файл
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
