import csvProcesor.CsvParser;
import csvProcesor.CsvParserException;
import xmlProcesor.XmlParser;
import xmlProcesor.XmlParserException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class SentenceParser {

    private final XmlParser xmlParser;
    private final CsvParser csvParser;
    private final InputReaderProvider inputReaderProvider;

    public SentenceParser(XmlParser xmlParser, CsvParser csvParser, InputReaderProvider inputReaderProvider) {
        this.xmlParser = xmlParser;
        this.csvParser = csvParser;
        this.inputReaderProvider = inputReaderProvider;
    }

    public static void main(String[] args) {
        SentenceParser parser = new SentenceParser(new XmlParser(), new CsvParser(), new InputReaderProvider());
        parser.run();
    }

    public void run() {
        Path inputFilePath = getInputFilePath();
        if (!verifyIfPathExists(inputFilePath)) {
            System.out.print("Given file does not exist. Please provide valid path.");
            return;
        }
        try {
            String inputFileName = getInputFileName(inputFilePath);

            xmlParser.init(inputFileName);
            csvParser.init(inputFileName);

            processFiles(inputFilePath);

        } catch (XmlParserException | CsvParserException e) {
            System.out.println("There was a problem: " + e.getMessage());
        }
        finally {
            xmlParser.finish();
            csvParser.finish();
        }
    }

    private Path getInputFilePath() {
        BufferedReader bufferedReader = inputReaderProvider.getConsoleInputReader();
        System.out.print("Please enter the location of text file: ");
        String userInput = null;
        try {
            userInput = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("Could not read input: " + e.getMessage());
        }
        return Paths.get(userInput);
    }

    protected boolean verifyIfPathExists(Path path) {
        return Files.exists(path);
    }

    private String getInputFileName(Path inputFilePath) {
        String fileName = inputFilePath.getFileName().toString();
        return fileName.replaceFirst("[.][^.]+$", "");
    }

    private void processFiles(Path inputFilePath) {
        try (BufferedReader reader = inputReaderProvider.getFileInputReader(inputFilePath)) {
            processSentencesInFiles(reader);
        } catch (IOException e) {
            System.out.println("Could not open input file: " + e.getMessage());
        }
    }

    private void processSentencesInFiles(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            xmlParser.processLine(line);
            csvParser.processLine(line);
        }
    }
}
