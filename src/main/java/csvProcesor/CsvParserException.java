package csvProcesor;

public class CsvParserException extends RuntimeException {

    public CsvParserException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
