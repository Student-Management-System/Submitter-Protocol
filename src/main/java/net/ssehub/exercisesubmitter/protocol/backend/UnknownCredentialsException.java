package net.ssehub.exercisesubmitter.protocol.backend;

/**
 * Exception denoting that the user could not login, because his/her credentials are wrong / unknown for the system.
 * @author El-Sharkawy
 *
 */
public class UnknownCredentialsException extends NetworkException {

    /**
     * Generated.
     */
    private static final long serialVersionUID = -4341652546362880628L;

    /**
     * Default constructor for this class.
     * @param message The error message of the Exception that occurred.
     */
    public UnknownCredentialsException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

}
