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
     * @deprecated Use {@link #getAbsolutePathInRepository()} or {@link #getAssignmentPath()}
     */
    public String[] getPath() {
        return path;
    }
    
    /**
     * Returns the absolute path (as one String with leading slash) of the assignment within the server.
     * This is equal to calling {@link #getPath()} and concatenating the segments with a slash sign. 
     * @return <tt>&#x2F; getPath()[0] &#x2F; getPath()[1]</tt>
     */
    public String getAbsolutePathInRepository() {
        return "/" + path[0] + "/" + path[1];
    }
    
    /**
     * Returns the absolute path (as one String with leading slash) of all submission targets for the specified
     * assignment. 
     * @return The folder containing all submissions.
     */
    public String getAssignmentPath() {
        return "/" + path[0];
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
