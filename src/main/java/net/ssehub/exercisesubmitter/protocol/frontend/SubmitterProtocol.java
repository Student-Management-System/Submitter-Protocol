package net.ssehub.exercisesubmitter.protocol.frontend;

import net.ssehub.exercisesubmitter.protocol.backend.LoginComponent;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkProtocol;
import net.ssehub.exercisesubmitter.protocol.backend.ServerNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.UnknownCredentialsException;

/**
 * Network protocol that provides API calls as required by the <b>Standalone / Eclise Exercise Submitter</b>.
 * Instances will store information (i.e., states) about the user and the course that is queried.
 * @author El-Sharkawy
 *
 */
public class SubmitterProtocol {
    private LoginComponent login;
    private boolean loggedIn;
    private NetworkProtocol protocol;
    
    /**
     * Creates a new {@link SubmitterProtocol} instance for a specific course. This <b>won't</b> login the user,
     * this has to be done via the {@link #login(String, String)} method.
     * @param authenticationURL The URL of the authentication server (aka Sparky service)
     * @param stdMgmtURL The URL of the student management service
     * @param courseName The course that is associated with the exercise submitter.
     */
    public SubmitterProtocol(String authenticationURL, String stdMgmtURL, String courseName) {
        login = new LoginComponent(authenticationURL, stdMgmtURL);
        protocol = new NetworkProtocol(stdMgmtURL, courseName);
        loggedIn = false;
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

}
