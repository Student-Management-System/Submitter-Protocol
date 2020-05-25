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
     * Computes the URL where to upload assignments.
     * @param assignment An assignment, which is currently in state submission
     * @return The absolute URL where to submit the specified assignment.
     * @throws NetworkException If network problems occur.
     * @see #getOpenAssignments()
     */
    public String getSubmissionUrl(Assignment assignment) throws NetworkException {
        String[] path = getPathToSubmission(assignment);
        return submissionServer + "/" + path[0] + "/" + path[1];
    }
   
    /**
     * Returns the destination path to a submission relative to the root of the repository.
     * @param assignment An assignment, to be submitted, replayed, or replayed after review.
     * @return An array of the form <tt>[assignment, user/group]</tt>.
     * @throws NetworkException If network problems occur.
     */
    public String[] getPathToSubmission(Assignment assignment) throws NetworkException {
        String[] path = new String[2];
        path[0] = assignment.getName();
        
        if (assignment.isGroupWork()) {
            // Get group for assignment
            String groupName = protocol.getGroupForAssignment(login.getUserID(), assignment.getID());
            path[1] = groupName;
        } else {
            path[1] = login.getUserName();
        }
        
        return path;
    }
}
