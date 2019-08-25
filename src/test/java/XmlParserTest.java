import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xmlProcesor.XmlParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class XmlParserTest {

    private static final String TEST_FILE_NAME = "testFileName";
    private XmlParser xmlParser;

    @Before
    public void setup() {
        xmlParser = new XmlParser();
    }

    @After
    public void cleanUp(){
        File resultFile = new File(TEST_FILE_NAME + ".xml");
        if (resultFile.exists()) {
            resultFile.delete();
        }
    }

    @Test
    public void createsXmlFile() {
        xmlParser.init(TEST_FILE_NAME);
        xmlParser.processLine("That's first line");
        xmlParser.finish();

        File resultFile = new File(TEST_FILE_NAME + ".xml");
        Assert.assertTrue(resultFile.exists());
    }

    @Test
    public void checksNumberOfLinesInResultFile() throws IOException {
        xmlParser.init(TEST_FILE_NAME);
        xmlParser.processLine("This is some line.");
        xmlParser.processLine("This is another line.");
        xmlParser.processLine("Test, test, @E#342534fr");
        xmlParser.processLine("Test, 你这肮脏的掠夺者");
        xmlParser.finish();

        Path path = Paths.get(TEST_FILE_NAME + ".xml");
        InputStream in = Files.newInputStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        Assert.assertEquals(24, reader.lines().count());
    }

    @Test
    public void checksHeaderInFile() throws IOException {
        xmlParser.init(TEST_FILE_NAME);
        xmlParser.processLine("This is sentence.");
        xmlParser.processLine("ldald oo3424^$%^e.");
        xmlParser.finish();

        Path path = Paths.get(TEST_FILE_NAME + ".xml");
        InputStream in = Files.newInputStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
        Assert.assertEquals(expectedResult, reader.readLine());
    }

    @Test
    public void checksContentOfResultFile() throws IOException {
        xmlParser.init(TEST_FILE_NAME);
        xmlParser.processLine("This is sentence.");
        xmlParser.finish();

        Path path = Paths.get(TEST_FILE_NAME + ".xml");
        InputStream in = Files.newInputStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String firstLine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
        String secondLine = "<text>";
        String thirdLine = "    <sentence>";
        String fourthLine = "        <word>is</word>";
        String fifthLine = "        <word>sentence</word>";
        String sixthLine = "        <word>This</word>";
        String seventhLine = "    </sentence>";
        String eighthLine = "</text>";

        Assert.assertEquals(firstLine, reader.readLine());
        Assert.assertEquals(secondLine, reader.readLine());
        Assert.assertEquals(thirdLine, reader.readLine());
        Assert.assertEquals(fourthLine, reader.readLine());
        Assert.assertEquals(fifthLine, reader.readLine());
        Assert.assertEquals(sixthLine, reader.readLine());
        Assert.assertEquals(seventhLine, reader.readLine());
        Assert.assertEquals(eighthLine, reader.readLine());
    }

    @Test
    public void checksSpecialCharacters() throws IOException {
        xmlParser.init(TEST_FILE_NAME);
        xmlParser.processLine("ãò? Sth ђёѓ.");
        xmlParser.finish();

        Path path = Paths.get(TEST_FILE_NAME + ".xml");
        InputStream in = Files.newInputStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String firstLine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
        String secondLine = "<text>";
        String thirdLine = "    <sentence>";
        String fourthLine = "        <word>Sth</word>";
        String fifthLine = "        <word>ãò?</word>";
        String sixthLine = "        <word>ђёѓ</word>";
        String seventhLine = "    </sentence>";
        String eighthLine = "</text>";

        Assert.assertEquals(firstLine, reader.readLine());
        Assert.assertEquals(secondLine, reader.readLine());
        Assert.assertEquals(thirdLine, reader.readLine());
        Assert.assertEquals(fourthLine, reader.readLine());
        Assert.assertEquals(fifthLine, reader.readLine());
        Assert.assertEquals(sixthLine, reader.readLine());
        Assert.assertEquals(seventhLine, reader.readLine());
        Assert.assertEquals(eighthLine, reader.readLine());
    }
}
