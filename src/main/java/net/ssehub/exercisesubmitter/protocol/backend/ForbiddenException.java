package net.ssehub.exercisesubmitter.protocol.backend;

/**
 * Denotes that an user that is logged in, is not allowed to query the requested API.
 * @author El-Sharkawy
 *
 */
public class ForbiddenException extends NetworkException {

    /**
     * Generated.
     */
    private static final long serialVersionUID = 7850393363799126217L;

    /**
     * Default constructor.
     * @param message The reason for the exception.
     */
    public ForbiddenException(String message) {
        super(message);
    }


}
