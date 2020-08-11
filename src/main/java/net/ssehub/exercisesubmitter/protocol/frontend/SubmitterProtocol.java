package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.LoginComponent;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkProtocol;
import net.ssehub.exercisesubmitter.protocol.backend.ServerNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.UnknownCredentialsException;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.StateEnum;

/**
 * Network protocol that provides API calls as required by the <b>Standalone / Eclipse Exercise Submitter</b>.
 * Instances will store information (i.e., states) about the user and the course that is queried.
 * @author El-Sharkawy
 *
 */
public class SubmitterProtocol {
    private LoginComponent login;
    private boolean loggedIn;
    private NetworkProtocol protocol;
    private String submissionServer;
    
    /**
     * Creates a new {@link SubmitterProtocol} instance for a specific course. This <b>won't</b> login the user,
     * this has to be done via the {@link #login(String, String)} method.
     * @param authenticationURL The URL of the authentication server (aka Sparky service)
     * @param stdMgmtURL The URL of the student management service
     * @param courseName The course that is associated with the exercise submitter.
     * @param submissionServer The root (URL) where to submit assignments (exercises).
     */
    public SubmitterProtocol(String authenticationURL, String stdMgmtURL, String courseName, String submissionServer) {
        login = new LoginComponent(authenticationURL, stdMgmtURL);
        protocol = new NetworkProtocol(stdMgmtURL, courseName);
        loggedIn = false;
        this.submissionServer = submissionServer;
    }
    
    /**
     * Allows to stub dependent network components for <b>testing</b>.
     * Will only replace components that are not <tt>null</tt>.
     * @param login The login component to use during tests.
     * @param protocol The component to simulate REST calls during tests.
     */
    void setNetworkComponents(LoginComponent login, NetworkProtocol protocol) {
        if (null != login) {
            this.login = login;
        }
        
        if (null != protocol) {
            this.protocol = protocol;
        }
    }
    
    /**
     * Logs the user in into the <b>student management system</b>.
     * @param userName The user name of the user to login.
     * @param password The password of the user to login.
     * @return <tt>true</tt> if the login was successful, <tt>false</tt> otherwise.
     * 
     * @throws UnknownCredentialsException If the credentials are wrong or the user is unknown by the system.
     * @throws ServerNotFoundException If one of the two servers is unreachable by the specified URLs.
     */
    public boolean login(String userName, String password) throws UnknownCredentialsException, ServerNotFoundException {
        loggedIn = login.login(userName, password);
        
        if (loggedIn) {
            protocol.setAccessToken(login.getManagementToken());
        }
        
        return loggedIn;
    }
    
    /**
     * Returns the internally used low-level protocol.
     * @return The low-level protocol.
     */
    protected NetworkProtocol getProtocol() {
        return protocol;
    }
    
    /**
     * Used to select the semester.
     * @param semester The semester to use (four lower case letters + 2 digits).
     */
    public void setSemester(String semester) {
        protocol.setSemester(semester);
    }
    
    /**
     * Returns the open list of assignments (in state submission), for the user.
     * @return The list of assignments, which may be currently be edited.
     * 
     * @throws NetworkException If network problems occur.
     */
    public List<Assignment> getOpenAssignments() throws NetworkException {
        return protocol.getAssignments(StateEnum.IN_PROGRESS);
    }
    
    /**
     * Returns the list of reviewed assignments, for the user.
     * @return The list of assignments, which are reviewed by the tutors.
     * 
     * @throws NetworkException If network problems occur.
     */
    public List<Assignment> getReviewedAssignments() throws NetworkException {
        return protocol.getAssignments(StateEnum.EVALUATED, StateEnum.CLOSED);
    }
    
    /**
     * Returns the list of reviewable assignments, for tutors.<br/>
     * <b style="color:red">Note:</b> This should only used inside the ExerciseReviewer, although is further protected
     * by the student management server. It may becomes confusing for students if they see this list.
     * @return The list of assignments, which may currently reviewed by the tutors.
     * 
     * @throws NetworkException If network problems occur.
     */
    public List<Assignment> getReviewableAssignments() throws NetworkException {
        return protocol.getAssignments(StateEnum.IN_REVIEW);
    }
    
    /**
     * Returns the destination path to a submission.
     * @param assignment An assignment, to be submitted, replayed, or replayed after review.
     * @return A specification that contains the target location where to submit the assignment.
     * @throws NetworkException If network problems occur.
     */
    public SubmissionTarget getPathToSubmission(Assignment assignment) throws NetworkException {
        SubmissionTarget target;
        if (assignment.isGroupWork()) {
            // Get group for assignment
            String groupName = protocol.getGroupForAssignment(login.getUserID(), assignment.getID());
            target = getPathToSubmission(assignment, groupName);
        } else {
            target = getPathToSubmission(assignment, login.getUserName());
        }
        
        return target;
    }
    
    /**
     * Returns the destination path to a submission.<br/>
     * <b style="color:red">Note:</b> This is intended to be used only be the ExerciseReviewer to retrieve the
     * {@link SubmissionTarget} for other users. Please use {@link #getPathToSubmission(Assignment)} inside the
     * ExerciseSubmitter.
     * @param assignment An assignment, to be submitted, replayed, or replayed after review.
     * @param submissionName The name of the user in case of an individual submission, or the group name in case
     *     of a group submission.
     * @return A specification that contains the target location where to submit the assignment.
     * @see #getPathToSubmission(Assignment)
     */
    protected SubmissionTarget getPathToSubmission(Assignment assignment, String submissionName) {
        return new SubmissionTarget(submissionServer, new String[] {assignment.getName(), submissionName});
    }
    
    /**
     * The ID of the user, used by the <b>student management system</b>.
     * @return the userID.
     */
    protected String getUserID() {
        return login.getUserID();
    }
}
