package net.ssehub.exercisesubmitter.protocol.backend;

/**
 * Network exception that is thrown if an unauthorized user queries an API that requires authorization.
 * @author El-Sharkawy
 *
 */
public class UnauthorizedException extends NetworkException {

    /**
     * Generated.
     */
    private static final long serialVersionUID = 5468298543017218073L;

    /**
     * Initializes an exception showing that the user need to be authorized for the current task, but isn't.
     * @param message The error message of the exception that occurred.
     */
    public UnauthorizedException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }
}
