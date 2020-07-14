package net.ssehub.exercisesubmitter.protocol.backend;

/**
 * An Network Exception occurs if there is a problem with the REST server.
 * 
 * @author Kunold
 *
 */
public abstract class NetworkException extends Exception {
    
    /**
     * Generated.
     */
    private static final long serialVersionUID = 1795210885665078124L;

    /**
     * An Exception that occurs when network problems occur.
     * @param message The error message of the Exception that occurred.
     */
    public NetworkException(String message) {
        super(message);
    }

}
