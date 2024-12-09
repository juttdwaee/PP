import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

public class Main {

    public static void main(String[] args) {
        // Спрашиваем пользователя, какой интерфейс использовать: CLI или GUI
        String[] options = {"CLI", "GUI"};
        int choice = JOptionPane.showOptionDialog(null, "Choose the interface", "Select Interface", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            // Запуск CLI (консольного интерфейса)
            runCLI();
        } else {
            // Запуск GUI (графического интерфейса)
            runGUI();
        }
    }

    public static void runCLI() {
        Scanner scanner = new Scanner(System.in);

        // Запрос данных у пользователя
        System.out.println("Enter input file name:");
        String inputFile = scanner.nextLine();
        System.out.println("Enter output file name:");
        String outputFile = scanner.nextLine();
        System.out.println("Enter input file type (txt, json, xml, yaml, html):");
        String inputFileType = scanner.nextLine();
        System.out.println("Enter output file type (txt, json, xml, yaml, html):");
        String outputFileType = scanner.nextLine();
        System.out.println("Would you like to encrypt the file? (yes/no):");
        String encrypt = scanner.nextLine();
        System.out.println("Would you like to archive the file? (yes/no):");
        String archive = scanner.nextLine();

        try {
            // Чтение входного файла
            String content = readFile(inputFile, inputFileType);
            System.out.println("Read content: " + content);

            // Запрос значений переменных у пользователя
            Map<String, Double> variables = getVariables(content, scanner);

            // Обработка арифметических выражений
            String processedContent = processArithmeticExpressions(content, variables);

            // Если нужно зашифровать файл
            if (encrypt.equalsIgnoreCase("yes")) {
                processedContent = encrypt(processedContent);
                System.out.println("File encrypted.");
            }

            // Запись результата в выходной файл
            writeFile(outputFile, outputFileType, processedContent);

            // Если нужно заархивировать файл
            if (archive.equalsIgnoreCase("yes")) {
                archiveFile(outputFile);
                System.out.println("File archived.");
            }

            System.out.println("File processed and saved as: " + outputFile);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        scanner.close();
    }

    public static String readFile(String filename, String fileType) throws Exception {
        System.out.println("Reading file: " + filename);
        switch (fileType.toLowerCase()) {
            case "txt": return readTextFile(filename);
            case "json": return readJsonFile(filename);
            case "xml": return readXmlFile(filename);
            case "yaml": return readYamlFile(filename);
            case "html": return readHtmlFile(filename);
            default: throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    public static void writeFile(String filename, String fileType, String content) throws Exception {
        System.out.println("Writing to file: " + filename);
        switch (fileType.toLowerCase()) {
            case "txt": writeTextFile(filename, content); break;
            case "json": writeJsonFile(filename, content); break;
            case "xml": writeXmlFile(filename, content); break;
            case "yaml": writeYamlFile(filename, content); break;
            case "html": writeHtmlFile(filename, content); break;
            default: throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    public static String readTextFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void writeTextFile(String filename, String content) throws IOException {
        Files.write(Paths.get(filename), content.getBytes());
    }

    public static String readJsonFile(String filename) throws IOException {
        // Реализуйте чтение JSON файла
        // (это пример, вам нужно использовать библиотеку для работы с JSON, например, Jackson или Gson)
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void writeJsonFile(String filename, String content) throws IOException {
        // Реализуйте запись в JSON файл
        Files.write(Paths.get(filename), content.getBytes());
    }

    public static String readXmlFile(String filename) throws Exception {
        // Пример для XML. Вам нужно использовать библиотеку для парсинга XML, например, JAXP или JAXB
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void writeXmlFile(String filename, String content) throws IOException {
        Files.write(Paths.get(filename), content.getBytes());
    }

    public static String readYamlFile(String filename) throws IOException {
        // Пример для YAML. Для парсинга YAML используйте библиотеку, например SnakeYAML
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void writeYamlFile(String filename, String content) throws IOException {
        Files.write(Paths.get(filename), content.getBytes());
    }

    public static String readHtmlFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void writeHtmlFile(String filename, String content) throws IOException {
        Files.write(Paths.get(filename), content.getBytes());
    }

    public static Map<String, Double> getVariables(String content, Scanner scanner) {
        Map<String, Double> variables = new HashMap<>();
        Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*"); // Регулярное выражение для переменных
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String variable = matcher.group();
            if (!variables.containsKey(variable)) {
                System.out.print("Enter value for variable '" + variable + "': ");
                double value = scanner.nextDouble();
                variables.put(variable, value);
            }
        }
        return variables;
    }

    public static String processArithmeticExpressions(String content, Map<String, Double> variables) {
        System.out.println("Processing arithmetic expressions...");
        String[] lines = content.split("\n");
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String processedLine = processLine(line, variables);
            result.append(processedLine).append("\n");
        }
        return result.toString();
    }

    public static String processLine(String line, Map<String, Double> variables) {
        // Заменяем переменные на их значения
        Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*"); // Поиск переменных
        Matcher matcher = pattern.matcher(line);
        StringBuffer processedLine = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group();
            if (variables.containsKey(variable)) {
                matcher.appendReplacement(processedLine, String.valueOf(variables.get(variable)));
            } else {
                matcher.appendReplacement(processedLine, variable); // Если переменной нет в списке, оставляем как есть
            }
        }
        matcher.appendTail(processedLine);

        String finalExpression = processedLine.toString();
        System.out.println("Final expression: " + finalExpression);

        try {
            double result = evaluateArithmeticExpression(finalExpression);
            return String.valueOf(result);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public static double evaluateArithmeticExpression(String expression) {
        try {
            Expression exp = new ExpressionBuilder(expression).build();
            return exp.evaluate();
        } catch (Exception e) {
            System.out.println("Error evaluating expression: " + expression);
            return 0;
        }
    }

    public static String encrypt(String content) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // Используем 128-битное шифрование
        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static void archiveFile(String fileName) throws IOException {
        String zipFileName = fileName + ".zip";
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            File fileToZip = new File(fileName);
            try (FileInputStream fis = new FileInputStream(fileToZip)) {
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zos.putNextEntry(zipEntry);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) >= 0) {
                    zos.write(buffer, 0, length);
                }
            }
        }
    }

    public static void runGUI() {
        JFrame frame = new JFrame("File Processor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));
        JTextField inputFileField = new JTextField();
        JTextField outputFileField = new JTextField();
        JTextField inputFileTypeField = new JTextField();
        JTextField outputFileTypeField = new JTextField();
        JCheckBox encryptCheckBox = new JCheckBox("Encrypt file");
        JCheckBox archiveCheckBox = new JCheckBox("Archive file");
        JButton processButton = new JButton("Process");
        panel.add(new JLabel("Input File:"));
        panel.add(inputFileField);
        panel.add(new JLabel("Output File:"));
        panel.add(outputFileField);
        panel.add(new JLabel("Input File Type:"));
        panel.add(inputFileTypeField);
        panel.add(new JLabel("Output File Type:"));
        panel.add(outputFileTypeField);
        panel.add(encryptCheckBox);
        panel.add(archiveCheckBox);
        panel.add(processButton);
        frame.add(panel);
        frame.setVisible(true);

        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Обработчик клика по кнопке Process
                String inputFile = inputFileField.getText();
                String outputFile = outputFileField.getText();
                String inputFileType = inputFileTypeField.getText();
                String outputFileType = outputFileTypeField.getText();
                String encrypt = encryptCheckBox.isSelected() ? "yes" : "no";
                String archive = archiveCheckBox.isSelected() ? "yes" : "no";

                // Вызываем основную логику обработки
                try {
                    // Чтение и обработка файла с GUI
                    String content = readFile(inputFile, inputFileType);
                    Map<String, Double> variables = getVariables(content, new Scanner(System.in));
                    String processedContent = processArithmeticExpressions(content, variables);
                    if (encrypt.equals("yes")) {
                        processedContent = encrypt(processedContent);
                    }
                    writeFile(outputFile, outputFileType, processedContent);
                    if (archive.equals("yes")) {
                        archiveFile(outputFile);
                    }
                    JOptionPane.showMessageDialog(frame, "File processed successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });
    }
}
