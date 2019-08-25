import csvProcesor.CsvParser;
import csvProcesor.CsvParserException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import xmlProcesor.XmlParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SentenceParserTest {

    private static final String TEST_FILE_NAME = "testFileName";
    private static final String TEST_FILE_PATH = "C:\\sth\\some_dir\\testFileName.txt";
    private static final String TEXT_TO_PARSE = "Text to test";
    private static final String EMPTY_STRING = "";
    private SentenceParser sentenceParser;


    @Mock
    private CsvParser csvParser;
    @Mock
    private XmlParser xmlParser;
    @Mock
    private InputReaderProvider inputReaderProvider;
    @Mock
    private BufferedReader consoleReader;
    @Mock
    private BufferedReader inputFileReader;

    @Before
    public void setup() throws IOException {
        initMocks(this);
        sentenceParser = new SentenceParserWraper(xmlParser, csvParser, inputReaderProvider, true);
        when(consoleReader.readLine()).thenReturn(TEST_FILE_PATH);
        when(inputReaderProvider.getConsoleInputReader()).thenReturn(consoleReader);
        when(inputFileReader.readLine())
                .thenReturn(TEXT_TO_PARSE)
                .thenReturn(null);
        when(inputReaderProvider.getFileInputReader(any(Path.class))).thenReturn(inputFileReader);
    }

    @Test
    public void initsParsers() {
        sentenceParser.run();
        verify(csvParser, times(1)).init(TEST_FILE_NAME);
        verify(xmlParser, times(1)).init(TEST_FILE_NAME);
    }

    @Test
    public void shouldNotCallParsersWhenInputFileNotFound() {
        sentenceParser = new SentenceParserWraper(xmlParser, csvParser, inputReaderProvider, false);
        sentenceParser.run();

        verifyZeroInteractions(csvParser);
        verifyZeroInteractions(xmlParser);
    }

    @Test
    public void shouldFinalizeAfterExceptionOccurred() {
        doThrow(new CsvParserException("sth", new RuntimeException())).when(csvParser).init(any());
        sentenceParser.run();
        verify(csvParser, times (1)).finish();
        verify(xmlParser, times (1)).finish();
    }

    @Test
    public void skipsEmptyLines() throws IOException {
        when(inputFileReader.readLine())
                .thenReturn(TEXT_TO_PARSE)
                .thenReturn(EMPTY_STRING)
                .thenReturn(EMPTY_STRING)
                .thenReturn(TEXT_TO_PARSE)
                .thenReturn(null);
        sentenceParser.run();
        verify(csvParser, times(2)).processLine(any());
        verify(xmlParser, times(2)).processLine(any());
    }

    private class SentenceParserWraper extends SentenceParser {

        private boolean fileExists;

        public SentenceParserWraper(XmlParser xmlParser, CsvParser csvParser, InputReaderProvider inputReaderProvider,
                                    boolean fileExists) {
            super(xmlParser, csvParser, inputReaderProvider);
            this.fileExists = fileExists;
        }

        @Override
        protected boolean verifyIfPathExists(Path path) {
            return fileExists;
        }
    }
}
