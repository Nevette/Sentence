import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class InputReaderProvider {

    public BufferedReader getFileInputReader(Path inputFilePath) throws IOException {
        InputStream inputFile = Files.newInputStream(inputFilePath);
        return new BufferedReader(new InputStreamReader(inputFile));
    }

    public BufferedReader getConsoleInputReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }
}
