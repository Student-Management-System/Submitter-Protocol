package net.ssehub.exercisesubmitter.protocol.frontend;

/**
 * Stores information where to submit an {@link Assignment} for the current user (student).
 * @author El-Sharkawy
 *
 */
public class SubmissionTarget {
    
    private String[] path;
    private String url;
    
    /**
     * Creates a new information where to submit an {@link Assignment}.
     * @param url The URL of the submission server.
     * @param path The relative path within the target server, where to submit the assignment.
     */
    SubmissionTarget(String url, String[] path) {
        this.url = url;
        this.path = path;
    }
    
    /**
     * Returns the target location of the assignment within the server.
     * Sub-folders are stored as single elements of the array.
     * @return The relative path <b>within</b> the target server, where to submit the assignment.
     */
    public String[] getPath() {
        return path;
    }
    
    /**
     * Returns the complete URL where to submit the assignment via SVN.
     * @return The full URL where to submit the assignment.
     */
    public String getSubmissionURL() {
        return url + "/" + path[0] + "/" + path[1];
    }
    
    @Override
    public String toString() {
        // For debugging only: Assignment of user/group
        return path[0] + " of " + path[1];
    }

}
