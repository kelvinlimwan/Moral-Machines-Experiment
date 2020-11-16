/**
 * InvalidInputException is an exception class that is thrown when the user types in anything other
 * than yes or no when asked for consent.
 * @author Kelvin Lim Wan
 */
public class InvalidInputException extends Exception {

    /**
     * Creates an invalid input exception with a default message.
     */
    public InvalidInputException() {
        super("Invalid Input!");
    }

    /**
     * Creates an invalid input exception with a default message.
     * @param message the pre-defined message.
     */
    public InvalidInputException(String message) {
        super(message);
    }
}
