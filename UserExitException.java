/**
 * UserExitException is an exception class that is thrown when the user wants to exit the program.
 * @author Kelvin Lim Wan
 */
public class UserExitException extends Exception {

    /**
     * Creates a user exit exception with a default message.
     */
    public UserExitException() {
        super("User wants to exit!");
    }

    /**
     * Creates a user exit exception with a pre-defined message.
     * @param message the pre-defined message.
     */
    public UserExitException(String message) {
        super(message);
    }
}
