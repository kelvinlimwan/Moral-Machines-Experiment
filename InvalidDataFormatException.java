public class InvalidDataFormatException extends Exception {

    public InvalidDataFormatException() {
        super("Invalid Data Format!");
    }

    public InvalidDataFormatException(String message) {
        super(message);
    }
}
