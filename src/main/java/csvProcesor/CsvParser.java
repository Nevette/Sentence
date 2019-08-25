package csvProcesor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CsvParser {

    private static final String INIT_ERROR = "Could not initialize CSV Parser";

    private PrintWriter outputTempFile;
    private int linesCounter = 0;
    private int wordsCounter = 0;
    private String inputFileName;

    public void init(String inputFile) throws CsvParserException {
        inputFileName = inputFile;
        try {
            outputTempFile = createTempCsvFile(inputFileName);
        } catch (Exception e) {
            throw new CsvParserException(INIT_ERROR, e);
        }
    }

    private PrintWriter createTempCsvFile(String inputFileName) throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(inputFileName + "_temp.csv")));
    }

    public void processLine(String line) {
        processSentence(line, outputTempFile);
        int lineLength = line.split("[\\s,.]+").length;
        if (lineLength > wordsCounter) {
            wordsCounter = lineLength;
        }
    }

    public void finish() {
        finalizeCSV(wordsCounter, outputTempFile, inputFileName);
    }

    private void finalizeCSV(int wordsCounter, PrintWriter outputTempFileCSV, String fileName) {
        outputTempFileCSV.flush();
        outputTempFileCSV.close();
        String header = setHeader(wordsCounter);
        Path tempFile = Paths.get(fileName + "_temp.csv");

        try (InputStream temporaryFile = Files.newInputStream(tempFile);
             PrintWriter outputCsvFile = createCsvFile(fileName);
             BufferedReader reader = createReader(temporaryFile)) {
            setHeaderAndWriteContent(header, outputCsvFile, reader);
            Files.delete(tempFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PrintWriter createCsvFile(String fileName) throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(fileName + ".csv")));
    }

    private BufferedReader createReader(InputStream temporaryFile) {
        return new BufferedReader(new InputStreamReader(temporaryFile));
    }

    private void setHeaderAndWriteContent(String header, PrintWriter outputCsvFile, BufferedReader reader) throws IOException {
        String line = null;
        outputCsvFile.println(header);
        while ((line = reader.readLine()) != null) {
            outputCsvFile.println(line);
        }
    }

    private void processSentence(String fileLine, PrintWriter outputTempFile) {
        linesCounter++;
        String finalString = Arrays.stream(fileLine.split("[\\s,.]+"))
                .sorted((s, t1) -> s.compareToIgnoreCase(t1))
                .collect(Collectors.joining(", "));
        outputTempFile.println("Sentence " + linesCounter + ", " + finalString);
    }

    private String setHeader(int wordsCounter) {
        String existingString = "";
        StringBuilder builder = new StringBuilder(existingString);
        for (int i = 1; i < wordsCounter + 1; i++) {
            builder.append(", Word " + i);
        }
        return builder.toString();
    }
}
