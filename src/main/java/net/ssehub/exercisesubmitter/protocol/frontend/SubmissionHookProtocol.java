package net.ssehub.exercisesubmitter.protocol.frontend;

import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;

/**
 * Review protocol for the SVN submission hook that automatically reviews student submissions and upload the results
 * to the <b>student management system</b>.
 * @author El-Sharkawy
 *
 */
public class SubmissionHookProtocol extends AbstractReviewerProtocol {

    /**
     * Creates a new {@link SubmissionHookProtocol} instance for a specific course.
     * @param authenticationURL The URL of the authentication server (aka Sparky service)
     * @param stdMgmtURL The URL of the student management service
     * @param courseName The course that is associated with the exercise submitter.
     * @param submissionServer The root (URL) where to submit assignments (exercises).
     */
    public SubmissionHookProtocol(String authenticationURL, String stdMgmtURL, String courseName,
        String submissionServer) {
        
        super(authenticationURL, stdMgmtURL, courseName, submissionServer);
    }
    
    /**
     * Returns the assignment with the specified name.
     * This method does not care about the state of the assignments to avoid errors if an assignment changes its
     * state during the submission.
     * @param assignmentName The name of the assignment to search for.
     * @return The assignment with the specified name.
     * @throws DataNotFoundException If there was no assignment with the specified name.
     * @throws NetworkException If network problems occur
     */
    public Assignment getAssignmentByName(String assignmentName) throws NetworkException {
        Assignment assignment = getProtocol().getAssignments().stream()
            .filter(a -> assignmentName.equals(a.getName()))
            .findAny()
            .orElseThrow(() -> new DataNotFoundException("No assignment was found with the specified name '"
                + assignmentName + "'.", assignmentName, DataNotFoundException.DataType.ASSIGNMENTS_NOT_FOUND));
        
        return assignment;
    }

}
