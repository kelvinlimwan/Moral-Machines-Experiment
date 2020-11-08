public class InvalidCharacteristicException extends Exception {

    public InvalidCharacteristicException() {
        super("Invalid Characteristic!");
    }

    public InvalidCharacteristicException(String message) {
        super(message);
    }
}
