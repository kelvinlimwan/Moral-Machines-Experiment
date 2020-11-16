/**
 * InvalidDataFormatException is an exception class that is thrown when there is an invalid number
 * of data fields per row.
 * @author Kelvin Lim Wan
 */
public class InvalidDataFormatException extends Exception {

    /**
     * Creates an invalid data format exception with a default message.
     */
    public InvalidDataFormatException() {
        super("Invalid Data Format!");
    }

    /**
     * Creates an invalid data format exception with a pre-defined message.
     * @param message the pre-defined message.
     */
    public InvalidDataFormatException(String message) {
        super(message);
    }
}
