import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class MainTests {

    private static final String TEST_JSON_FILE = "test.json";
    private static final String TEST_OUTPUT_JSON_FILE = "output.json";
    private static final String TEST_TEXT_FILE = "test.txt";
    private static final String TEST_OUTPUT_TEXT_FILE = "output.txt";
    private static final String TEST_HTML_FILE = "test.html";
    private static final String TEST_OUTPUT_HTML_FILE = "output.html";
    private static final String TEST_XML_FILE = "test.xml";
    private static final String TEST_OUTPUT_XML_FILE = "output.xml";
    private static final String TEST_YAML_FILE = "test.yaml";
    private static final String TEST_OUTPUT_YAML_FILE = "output.yaml";

    @BeforeEach
    public void setup() throws IOException {
        // Создание тестового JSON файла
        String testJsonContent = "{ \"name\": \"Test\", \"value\": 123 }";
        Files.write(Paths.get(TEST_JSON_FILE), testJsonContent.getBytes());

        // Создание тестового текстового файла
        String testTextContent = "Hello, world!";
        Files.write(Paths.get(TEST_TEXT_FILE), testTextContent.getBytes());

        // Создание тестового HTML файла
        String testHtmlContent = "<html><body><h1>Hello, world!</h1></body></html>";
        Files.write(Paths.get(TEST_HTML_FILE), testHtmlContent.getBytes());

        // Создание тестового XML файла
        String testXmlContent = "<root><name>Test</name><value>123</value></root>";
        Files.write(Paths.get(TEST_XML_FILE), testXmlContent.getBytes());

        // Создание тестового YAML файла
        String testYamlContent = "name: Test\nvalue: 123";
        Files.write(Paths.get(TEST_YAML_FILE), testYamlContent.getBytes());
    }

    @AfterEach
    public void cleanup() throws IOException {
        // Удаление тестовых файлов после каждого теста
        Files.deleteIfExists(Paths.get(TEST_JSON_FILE));
        Files.deleteIfExists(Paths.get(TEST_OUTPUT_JSON_FILE));
        Files.deleteIfExists(Paths.get(TEST_TEXT_FILE));
        Files.deleteIfExists(Paths.get(TEST_OUTPUT_TEXT_FILE));
        Files.deleteIfExists(Paths.get(TEST_HTML_FILE));
        Files.deleteIfExists(Paths.get(TEST_OUTPUT_HTML_FILE));
        Files.deleteIfExists(Paths.get(TEST_XML_FILE));
        Files.deleteIfExists(Paths.get(TEST_OUTPUT_XML_FILE));
        Files.deleteIfExists(Paths.get(TEST_YAML_FILE));
        Files.deleteIfExists(Paths.get(TEST_OUTPUT_YAML_FILE));
    }

    @Test
    public void testReadJsonFile() throws Exception {
        // Тестирование чтения JSON файла
        String content = Main.readJsonFile(TEST_JSON_FILE);
        assertTrue(content.contains("Test"), "JSON content should contain 'Test'");
        assertTrue(content.contains("123"), "JSON content should contain '123'");
    }

    @Test
    public void testWriteJsonFile() throws Exception {
        // Тестирование записи JSON файла
        String content = "{ \"name\": \"Output\", \"value\": 456 }";
        Main.writeJsonFile(TEST_OUTPUT_JSON_FILE, content);

        String writtenContent = new String(Files.readAllBytes(Paths.get(TEST_OUTPUT_JSON_FILE)));
        assertTrue(writtenContent.contains("Output"), "Output JSON should contain 'Output'");
        assertTrue(writtenContent.contains("456"), "Output JSON should contain '456'");
    }

    @Test
    public void testReadTextFile() throws IOException {
        // Тестирование чтения текстового файла
        String content = Main.readTextFile(TEST_TEXT_FILE);
        assertEquals("Hello, world!", content, "Text content should match the expected text");
    }

    @Test
    public void testWriteTextFile() throws IOException {
        // Тестирование записи текстового файла
        String content = "This is a test text file.";
        Main.writeTextFile(TEST_OUTPUT_TEXT_FILE, content);

        String writtenContent = new String(Files.readAllBytes(Paths.get(TEST_OUTPUT_TEXT_FILE)));
        assertEquals(content, writtenContent, "Text content should match the expected content");
    }

    @Test
    public void testReadHtmlFile() throws IOException {
        // Тестирование чтения HTML файла
        String content = Main.readHtmlFile(TEST_HTML_FILE);
        assertTrue(content.contains("<h1>Hello, world!</h1>"), "HTML content should contain '<h1>Hello, world!</h1>'");
    }

    @Test
    public void testWriteHtmlFile() throws IOException {
        // Тестирование записи HTML файла
        String content = "<html><body><h1>Output</h1></body></html>";
        Main.writeHtmlFile(TEST_OUTPUT_HTML_FILE, content);

        String writtenContent = new String(Files.readAllBytes(Paths.get(TEST_OUTPUT_HTML_FILE)));
        assertTrue(writtenContent.contains("<h1>Output</h1>"), "Output HTML should contain '<h1>Output</h1>'");
    }

    @Test
    public void testReadXmlFile() throws IOException {
        // Тестирование чтения XML файла
        String content = Main.readXmlFile(TEST_XML_FILE);
        assertTrue(content.contains("<name>Test</name>"), "XML content should contain '<name>Test'</name>");
        assertTrue(content.contains("<value>123</value>"), "XML content should contain '<value>123'</value>");
    }

    @Test
    public void testWriteXmlFile() throws IOException {
        // Тестирование записи XML файла
        String content = "<root><name>Output</name><value>456</value></root>";
        Main.writeXmlFile(TEST_OUTPUT_XML_FILE, content);

        String writtenContent = new String(Files.readAllBytes(Paths.get(TEST_OUTPUT_XML_FILE)));
        assertTrue(writtenContent.contains("<name>Output</name>"), "Output XML should contain '<name>Output'</name>");
        assertTrue(writtenContent.contains("<value>456</value>"), "Output XML should contain '<value>456'</value>");
    }

    @Test
    public void testReadYamlFile() throws IOException {
        // Тестирование чтения YAML файла
        String content = Main.readYamlFile(TEST_YAML_FILE);
        assertTrue(content.contains("name: Test"), "YAML content should contain 'name: Test'");
        assertTrue(content.contains("value: 123"), "YAML content should contain 'value: 123'");
    }

    @Test
    public void testWriteYamlFile() throws IOException {
        // Тестирование записи YAML файла
        String content = "name: Output\nvalue: 456";
        Main.writeYamlFile(TEST_OUTPUT_YAML_FILE, content);

        String writtenContent = new String(Files.readAllBytes(Paths.get(TEST_OUTPUT_YAML_FILE)));
        assertTrue(writtenContent.contains("name: Output"), "Output YAML should contain 'name: Output'");
        assertTrue(writtenContent.contains("value: 456"), "Output YAML should contain 'value: 456'");
    }

    @Test
    public void testEncrypt() throws Exception {
        // Тестирование шифрования
        String content = "Sensitive Data";
        String encryptedContent = Main.encrypt(content);
        assertNotEquals(content, encryptedContent, "Encrypted content should be different from original content");

        // Дополнительно: проверка на успешное дешифрование
        String decryptedContent = Main.decrypt(encryptedContent);
        assertEquals(content, decryptedContent, "Decrypted content should match the original content");
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        // Тестирование шифрования и дешифрования с одним ключом
        String content = "Test Content";
        String encryptedContent = Main.encrypt(content);

        // Дешифрование с тем же ключом
        String decryptedContent = Main.decrypt(encryptedContent);
        assertEquals(content, decryptedContent, "Decrypted content should match original content");
    }

    @Test
    public void testArchiveFile() throws IOException {
        // Тестирование архивирования файла
        String content = "This is a test file for archiving.";
        Files.write(Paths.get(TEST_TEXT_FILE), content.getBytes());

        Main.archiveFile(TEST_TEXT_FILE);

        // Проверяем, что архивный файл существует
        File zipFile = new File(TEST_TEXT_FILE + ".zip");
        assertTrue(zipFile.exists(), "The zip file should be created.");
    }

    @Test
    public void testProcessArithmeticExpression() {
        // Тестирование обработки арифметических выражений
        String expression = "a + b * c";
        Map<String, Double> variables = new HashMap<>();
        variables.put("a", 1.0);
        variables.put("b", 2.0);
        variables.put("c", 3.0);

        String result = Main.processArithmeticExpressions(expression, variables);
        assertEquals("7.0", result, "Arithmetic expression should be processed correctly");
    }
}
