import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class MainTest {

    @Test
    void testReadTextFile() throws IOException {
        String testFileName = "test.txt";
        String testContent = "Hello, world!";
        Files.write(Paths.get(testFileName), testContent.getBytes());
        String result = Main.readTextFile(testFileName);
        assertEquals(testContent, result);
        Files.delete(Paths.get(testFileName));
    }

    @Test
    void testWriteTextFile() throws IOException {
        String testFileName = "output.txt";
        String testContent = "This is a test.";
        Main.writeTextFile(testFileName, testContent);
        String result = new String(Files.readAllBytes(Paths.get(testFileName)));
        assertEquals(testContent, result);
        Files.delete(Paths.get(testFileName));
    }

    @Test
    void testProcessArithmeticExpressions() {
        String content = "a + b * 2";
        Map<String, Double> variables = Map.of("a", 3.0, "b", 4.0);
        String result = Main.processArithmeticExpressions(content, variables);
        assertEquals("11.0", result.trim());
    }

    @Test
    void testEncrypt() throws Exception {
        String content = "Sensitive data";
        String encryptedContent = Main.encrypt(content);
        assertNotEquals(content, encryptedContent);
        assertNotNull(encryptedContent);
    }

    @Test
    void testArchiveFile() throws IOException {
        String testFileName = "test.txt";
        String testContent = "Archive this content.";
        Files.write(Paths.get(testFileName), testContent.getBytes());
        Main.archiveFile(testFileName);
        File archive = new File(testFileName + ".zip");
        assertTrue(archive.exists());
        Files.delete(Paths.get(testFileName));
        archive.delete();
    }

    @Test
    void testEvaluateArithmeticExpression() {
        double result = Main.evaluateArithmeticExpression("3 + 4 * 2");
        assertEquals(11.0, result);
    }

    @Test
    void testGetVariables() {
        String content = "x + y";
        Scanner scanner = new Scanner("2\n3\n");
        Map<String, Double> variables = Main.getVariables(content, scanner);
        assertEquals(2.0, variables.get("x"));
        assertEquals(3.0, variables.get("y"));
    }

    @Test
    void testProcessLine() {
        String line = "x * y + z";
        Map<String, Double> variables = Map.of("x", 2.0, "y", 3.0, "z", 4.0);
        String result = Main.processLine(line, variables);
        assertEquals("10.0", result);
    }
}
