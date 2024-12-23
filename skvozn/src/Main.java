import com.fasterxml.jackson.databind.ObjectMapper;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.yaml.snakeyaml.Yaml;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;
import java.util.zip.*;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.github.junrar.exception.RarException;



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
        System.out.println("Enter input file type (txt, json, xml, yaml, html, rar):");
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
            case "rar": return readRarFile(filename); // Чтение rar файлов
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

    // Реализация чтения и записи для разных форматов

    // Чтение текстового файла
    public static String readTextFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void writeTextFile(String filename, String content) throws IOException {
        Files.write(Paths.get(filename), content.getBytes());
    }

    // Чтение JSON файла
    public static String readJsonFile(String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        objectMapper.readTree(content);  // Проверка на валидность JSON
        return content;
    }

    public static void writeJsonFile(String filename, String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.readTree(content);  // Проверка на валидность JSON
        Files.write(Paths.get(filename), content.getBytes());
    }

    // Чтение XML файла
    public static String readXmlFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void writeXmlFile(String filename, String content) throws IOException {
        Files.write(Paths.get(filename), content.getBytes());
    }

    // Чтение YAML файла с использованием SnakeYAML
    public static String readYamlFile(String filename) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = Files.newInputStream(Paths.get(filename))) {
            Object data = yaml.load(inputStream);
            return yaml.dump(data);  // Преобразуем YAML обратно в строку
        }
    }

    public static void writeYamlFile(String filename, String content) throws IOException {
        Yaml yaml = new Yaml();
        try (FileWriter writer = new FileWriter(filename)) {
            yaml.dump(content, writer);
        }
    }

    // Чтение HTML файла
    public static String readHtmlFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void writeHtmlFile(String filename, String content) throws IOException {
        Files.write(Paths.get(filename), content.getBytes());
    }

    // Чтение RAR файла
    public static String readRarFile(String filename) throws IOException {
        File rarFile = new File(filename);
        File tempDir = new File("temp_extract");
        if (!tempDir.exists()) tempDir.mkdir();

        try (Archive archive = new Archive(rarFile)) {
            FileHeader fileHeader;
            while ((fileHeader = archive.nextFileHeader()) != null) {
                if (fileHeader.isDirectory()) {
                    continue;
                }

                File outFile = new File(tempDir, fileHeader.getFileNameString());
                outFile.getParentFile().mkdirs();

                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    archive.extractFile(fileHeader, fos);
                }
            }
        } catch (RarException e) {
            throw new RuntimeException(e);
        }
        return "Extracted to temp_extract directory.";
    }

    public static Map<String, Double> getVariables(String content, Scanner scanner) {
        Map<String, Double> variables = new HashMap<>();

        // Удаляем теги из текста
        String contentWithoutTags = content.replaceAll("<[^>]+>", ""); // Убираем все HTML/XML теги

        // Теперь ищем переменные в тексте без тегов
        Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(contentWithoutTags);

        // Проходим по всем найденным переменным и просим пользователя ввести их значения
        Set<String> variableSet = new HashSet<>();
        while (matcher.find()) {
            variableSet.add(matcher.group());
        }

        List<String> variableList = new ArrayList<>(variableSet);

        // Создаем графический интерфейс для ввода значений переменных
        JPanel panel = new JPanel(new GridLayout(variableList.size(), 2));

        // Для каждой переменной создаем текстовое поле
        for (String variable : variableList) {
            panel.add(new JLabel(variable));
            panel.add(new JTextField(10));
        }

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter values for variables", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < variableList.size(); i++) {
                String variable = variableList.get(i);
                String inputValue = ((JTextField) panel.getComponent(i * 2 + 1)).getText();
                variables.put(variable, Double.parseDouble(inputValue));
            }
        }

        return variables;
    }

    public static Map<String, Double> getVariablesViaGUI(String content) {
        Map<String, Double> variables = new HashMap<>();

        // Удаляем теги из текста
        String contentWithoutTags = content.replaceAll("<[^>]+>", ""); // Убираем все HTML/XML теги

        // Используем регулярное выражение для поиска переменных в очищенном тексте
        Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(contentWithoutTags);
        Set<String> variableSet = new HashSet<>();

        // Собираем все уникальные переменные из текста
        while (matcher.find()) {
            variableSet.add(matcher.group());
        }

        // Создаем панель для ввода значений всех переменных
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(variableSet.size(), 2)); // Сетка с 2 столбцами: имя переменной и поле ввода

        Map<String, JTextField> textFields = new HashMap<>();

        for (String variable : variableSet) {
            panel.add(new JLabel("Enter value for variable '" + variable + "':"));
            JTextField field = new JTextField(10);
            textFields.put(variable, field);
            panel.add(field);
        }

        int result = JOptionPane.showConfirmDialog(null, panel, "Variable Input", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Заполняем переменные значениями, введенными пользователем
                for (String variable : variableSet) {
                    String value = textFields.get(variable).getText();
                    if (value.isEmpty()) {
                        throw new NumberFormatException("Value for variable '" + variable + "' is empty!");
                    }
                    variables.put(variable, Double.parseDouble(value));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return getVariablesViaGUI(content); // Рекурсия для повторного ввода значений
            }
        } else {
            JOptionPane.showMessageDialog(null, "Operation cancelled.", "Cancelled", JOptionPane.WARNING_MESSAGE);
        }

        return variables;
    }

    public static String processArithmeticExpressions(String content, Map<String, Double> variables) {
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
        Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(line);
        StringBuffer processedLine = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group();
            if (variables.containsKey(variable)) {
                matcher.appendReplacement(processedLine, String.valueOf(variables.get(variable)));
            } else {
                matcher.appendReplacement(processedLine, variable);
            }
        }
        matcher.appendTail(processedLine);

        String finalExpression = processedLine.toString();
        try {
            double result = evaluateArithmeticExpression(finalExpression);
            return String.valueOf(result);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public static double evaluateArithmeticExpression(String expression) {
        Expression exp = new ExpressionBuilder(expression).build();
        return exp.evaluate();
    }

    // Шифрование текста с использованием AES
    public static String encrypt(String content) throws Exception {
        String key = "1234567890123456"; // Ключ должен быть длиной 16 символов
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(content.getBytes());

        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Дешифрование текста с использованием AES
    public static String decrypt(String encryptedContent) throws Exception {
        String key = "1234567890123456"; // Ключ должен быть длиной 16 символов
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedContent);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }

    // Архивация файла в ZIP
    public static void archiveFile(String filename) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(filename + ".zip"));
             FileInputStream fileIn = new FileInputStream(filename)) {

            ZipEntry zipEntry = new ZipEntry(new File(filename).getName());
            zipOut.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileIn.read(buffer)) >= 0) {
                zipOut.write(buffer, 0, length);
            }

            zipOut.closeEntry();
        }
    }

    public static void runGUI() {
        JFrame frame = new JFrame("File Processor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        // Основная панель
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Панель для выбора файлов
        JPanel filePanel = new JPanel(new GridLayout(2, 3, 10, 10));
        filePanel.setBorder(BorderFactory.createTitledBorder("File Settings"));
        filePanel.add(new JLabel("Input File:"));
        JTextField inputFileField = new JTextField();
        filePanel.add(inputFileField);
        JButton inputBrowseButton = new JButton("Browse");
        filePanel.add(inputBrowseButton);

        filePanel.add(new JLabel("Output File:"));
        JTextField outputFileField = new JTextField();
        filePanel.add(outputFileField);
        JButton outputBrowseButton = new JButton("Browse");
        filePanel.add(outputBrowseButton);

        // Панель для выбора форматов
        JPanel formatPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formatPanel.setBorder(BorderFactory.createTitledBorder("File Types"));
        formatPanel.add(new JLabel("Input File Type:"));
        JComboBox<String> inputFileTypeComboBox = new JComboBox<>(new String[]{"txt", "json", "xml", "yaml", "html", "rar"});
        formatPanel.add(inputFileTypeComboBox);

        formatPanel.add(new JLabel("Output File Type:"));
        JComboBox<String> outputFileTypeComboBox = new JComboBox<>(new String[]{"txt", "json", "xml", "yaml", "html"});
        formatPanel.add(outputFileTypeComboBox);

        // Панель для опций
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
        JCheckBox encryptCheckBox = new JCheckBox("Encrypt file");
        JCheckBox archiveCheckBox = new JCheckBox("Archive file");
        optionsPanel.add(encryptCheckBox);
        optionsPanel.add(archiveCheckBox);

        // Кнопка обработки
        JButton processButton = new JButton("Process");
        processButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Логирование/вывод результата
        JTextArea logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Log"));

        // Добавляем элементы на главную панель
        mainPanel.add(filePanel);
        mainPanel.add(formatPanel);
        mainPanel.add(optionsPanel);
        mainPanel.add(processButton);
        mainPanel.add(logScrollPane);

        // Добавляем панель в окно
        frame.add(mainPanel);
        frame.setVisible(true);

        // Обработчики событий
        inputBrowseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                inputFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        outputBrowseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                outputFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        processButton.addActionListener(e -> {
            String inputFile = inputFileField.getText();
            String outputFile = outputFileField.getText();
            String inputFileType = (String) inputFileTypeComboBox.getSelectedItem();
            String outputFileType = (String) outputFileTypeComboBox.getSelectedItem();
            boolean encrypt = encryptCheckBox.isSelected();
            boolean archive = archiveCheckBox.isSelected();

            try {
                // Чтение файла
                String content = readFile(inputFile, inputFileType);
                logArea.append("File read successfully.\n");

                // Запрос значений переменных
                Map<String, Double> variables = getVariablesViaGUI(content);
                logArea.append("Variables entered successfully.\n");

                // Обработка арифметических выражений
                String processedContent = processArithmeticExpressions(content, variables);

                // Шифрование (если выбрано)
                if (encrypt) {
                    processedContent = encrypt(processedContent);
                    logArea.append("File encrypted.\n");
                }

                // Запись в файл
                writeFile(outputFile, outputFileType, processedContent);
                logArea.append("File written successfully.\n");

                // Архивация (если выбрано)
                if (archive) {
                    archiveFile(outputFile);
                    logArea.append("File archived successfully.\n");
                }

                logArea.append("Processing completed.\n");
            } catch (Exception ex) {
                logArea.append("Error: " + ex.getMessage() + "\n");
            }
        });
    }

}