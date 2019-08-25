import csvProcesor.CsvParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CsvParserTest {

    private static final String TEST_FILE_NAME = "testFileName";
    private CsvParser csvParser;

    @Before
    public void setup() {
        csvParser = new CsvParser();

    }

    @After
    public void cleanUp() {
        File resultFile = new File(TEST_FILE_NAME + ".csv");
        if (resultFile.exists()) {
            resultFile.delete();
        }
    }

    @Test
    public void createsCsvFile() {
        csvParser.init(TEST_FILE_NAME);
        csvParser.processLine("This is first line.");
        csvParser.finish();
        File resultFile = new File(TEST_FILE_NAME + ".csv");
        Assert.assertTrue(resultFile.exists());
    }

    @Test
    public void checksLinesInResultFile() throws IOException {
        csvParser.init(TEST_FILE_NAME);
        csvParser.processLine("This is some line.");
        csvParser.processLine("This is another line.");
        csvParser.processLine("Test, test, @E#342534fr");
        csvParser.processLine("**656we 3@E#6564, 'sd[]wqe[");
        csvParser.finish();

        Path path = Paths.get(TEST_FILE_NAME + ".csv");
        InputStream in = Files.newInputStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        Assert.assertEquals(5, reader.lines().count());
    }

    @Test
    public void checksSpecialCharacters() throws IOException {
        csvParser.init(TEST_FILE_NAME);
        csvParser.processLine("ãò? Sth ђёѓ.");
        csvParser.finish();

        Path path = Paths.get(TEST_FILE_NAME + ".csv");
        InputStream in = Files.newInputStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        reader.readLine();
        String line = reader.readLine();
        Assert.assertEquals("Sentence 1, Sth, ãò?, ђёѓ", line);
    }

    @Test
    public void addsHeaderToFile() throws IOException {
        csvParser.init(TEST_FILE_NAME);
        csvParser.processLine("This is first line.");
        csvParser.finish();

        Path path = Paths.get(TEST_FILE_NAME + ".csv");
        InputStream in = Files.newInputStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line = reader.readLine();
        Assert.assertEquals(", Word 1, Word 2, Word 3, Word 4", line);
    }

    @Test
    public void deletesTempFile() {
        csvParser.init(TEST_FILE_NAME);
        csvParser.processLine("This is some line.");

        File tempFile = new File(TEST_FILE_NAME + "_temp.csv");
        Assert.assertTrue(tempFile.exists());

        csvParser.finish();
        Assert.assertFalse(tempFile.exists());
    }

    @Test
    public void addsSentenceWordAtTheBeginningAndSortsWords() throws IOException {
        csvParser.init(TEST_FILE_NAME);
        csvParser.processLine("This is first line.");
        csvParser.processLine("This is second line.");
        csvParser.finish();

        Path path = Paths.get(TEST_FILE_NAME + ".csv");
        InputStream in = Files.newInputStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        reader.readLine();
        String line = reader.readLine();
        Assert.assertEquals("Sentence 1, first, is, line, This", line);
    }
}
