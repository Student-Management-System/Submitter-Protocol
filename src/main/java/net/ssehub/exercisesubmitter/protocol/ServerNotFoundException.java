package net.ssehub.exercisesubmitter.protocol;


/**
 * Declares an exception for the case, that the REST server is not found.
 * 
 * @author Kunold
 *
 */
public class ServerNotFoundException extends NetworkException {

    /**
     * Generated id.
     */
    private static final long serialVersionUID = 6447226795474482484L;
    
    /**
     * The url of the REST server.
     */
    private String url;
    
    /**
     * A Exception that occurs if the REST server is not found.
     * @param message the error message that occurred.
     * @param url the url of the REST server.
     */
    public ServerNotFoundException(String message, String url) {
        super(message);
        this.url = url;
    }

    /**
     * Getter for the url of the REST server.
     * @return the url of the REST server.
     */
    public String getURL() {
        return url;
    }
}
