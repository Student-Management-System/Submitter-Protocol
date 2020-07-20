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
     * Returns the absolute path (as one String with leading slash) of the assignment within the server.
     * This is equal to calling {@link #getPath()} and concatenating the segments with a slash sign. 
     * @return <tt>&#x2F; getAssignmentName() &#x2F; getSubmissionPath()</tt>
     */
    public String getAbsolutePathInRepository() {
        return "/" + path[0] + "/" + path[1];
    }
    
    /**
     * Returns the absolute path / name of all submission targets for the specified
     * assignment. 
     * As this method may also be used to retrieve the folder <b>name</b> this method does not append a leading slash. 
     * @return The folder containing all submissions.
     */
    public String getAssignmentName() {
        return path[0];
    }
    
    /**
     * Returns the relative path / name of the submission target within {@link #getAssignmentName()}.
     * @return The folder containing all submissions.
     */
    public String getSubmissionPath() {
        return path[1];
    }
    
    /**
     * Returns the complete URL where to submit an individual submission via SVN.
     * @return The full URL where to submit the assignment.
     */
    public String getSubmissionURL() {
        return url + "/" + path[0] + "/" + path[1];
    }
    
    /**
     * Returns the complete URL where all submissions of all participants are stored via SVN.<br/>
     * <b style="color:red">Note: </b> This is intended for the ExerciseReviewer only.
     * @return The full URL where to submit the assignment.
     */
    public String getAllSubmissionsURL() {
        return url + "/" + path[0] + "/" + path[1];
    }
    
    @Override
    public String toString() {
        // For debugging only: Assignment of user/group
        return path[0] + " of " + path[1];
    }

}
