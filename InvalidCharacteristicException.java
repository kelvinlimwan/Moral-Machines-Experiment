/**
 * InvalidCharacteristicException is an exception class that is thrown when a field value is
 * invalid.
 * @author Kelvin Lim Wan
 */
public class InvalidCharacteristicException extends Exception {

    /**
     * Creates an invalid characteristic exception with a default message.
     */
    public InvalidCharacteristicException() {
        super("Invalid Characteristic!");
    }

    /**
     * Creates an invalid characteristic exception with a pre-defined message.
     * @param message the pre-defined message.
     */
    public InvalidCharacteristicException(String message) {
        super(message);
    }


}
