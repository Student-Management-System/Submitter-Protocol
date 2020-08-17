package net.ssehub.exercisesubmitter.protocol.backend;

import java.net.ConnectException;

import net.ssehub.studentmgmt.backend_api.ApiClient;
import net.ssehub.studentmgmt.backend_api.api.AuthenticationApi;
import net.ssehub.studentmgmt.backend_api.model.AuthSystemCredentials;
import net.ssehub.studentmgmt.backend_api.model.AuthTokenDto;
import net.ssehub.studentmgmt.sparkyservice_api.ApiException;
import net.ssehub.studentmgmt.sparkyservice_api.api.AuthControllerApi;
import net.ssehub.studentmgmt.sparkyservice_api.model.AuthenticationInfoDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.CredentialsDto;

/**
 * Responsible component to login the user into the student management system.
 * @author El-Sharkawy
 *
 */
public class LoginComponent {
    
    /**
     * ApiClinet for the <b>Authentication system </b> (aka. SparyService).
     * The ApiClient enables to set a BasePath for the other API`s.
     */
    private net.ssehub.studentmgmt.sparkyservice_api.ApiClient sparkyClient;
    private AuthControllerApi authApi;
    private String authenticationURL;
    
    /**
     * ApiClinet for the <b>Student Management system</b>.
     * The ApiClient enables to set a BasePath for the other API`s.
     */
    private ApiClient stdMgmtClient;
    private AuthenticationApi mgmtAuthApi;
    private String stdMgmtURL;
    
    // User Data
    private String userName;
    private String managementToken;
    private String userID;
    
    /**
     * Instantiates the {@link LoginComponent} by specifying the authentication and student management service to use.
     * @param authenticationURL The URL of the authentication server (aka Sparky service)
     * @param stdMgmtURL The URL of the student management service
     */
    public LoginComponent(String authenticationURL, String stdMgmtURL) {
        // Sparky Service to retrieve token
        sparkyClient = new net.ssehub.studentmgmt.sparkyservice_api.ApiClient();
        sparkyClient.setBasePath(authenticationURL);
        authApi = new AuthControllerApi(sparkyClient);
        this.authenticationURL = authenticationURL;
        
        // Student Management system to query for management data
        stdMgmtClient = new ApiClient();
        stdMgmtClient.setBasePath(stdMgmtURL);
        mgmtAuthApi = new AuthenticationApi(stdMgmtClient);
        this.stdMgmtURL = stdMgmtURL;
    }
    
    /**
     * Logs the user in into the <b>student management system</b> via the <b>authentication service</b>.
     * @param userName The user name of the user to login.
     * @param password The password of the user to login.
     * @return <tt>true</tt> if the login was successful, <tt>false</tt> otherwise.
     * 
     * @throws UnknownCredentialsException If the credentials are wrong or the user is unknown by the system.
     * @throws ServerNotFoundException If one of the two servers is unreachable by the specified URLs.
     */
    public boolean login(String userName, String password) throws UnknownCredentialsException, ServerNotFoundException {
        CredentialsDto credentials = new CredentialsDto();
        credentials.setUsername(userName);
        credentials.setPassword(password);
        
        // Login into SparkyService to retrieve usable token
        AuthenticationInfoDto authInfo = null;
        String tmpToken = null;
        try {
            authInfo = authApi.authenticate(credentials);
            this.userName = authInfo.getUser().getUsername();
            tmpToken = authInfo.getToken().getToken();
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), authenticationURL);
        } catch (ApiException e) {
            if (e.getCause() instanceof ConnectException) {
                throw new ServerNotFoundException(e.getMessage(), authenticationURL);
            }
            throw new UnknownCredentialsException("Could not login \"" + userName
                + "\", credentials are unknown. Please check that user exist.");
        }
        
        if (null != tmpToken && null != authInfo) {
            AuthSystemCredentials tokenAsJson = new AuthSystemCredentials();
            tokenAsJson.setToken(tmpToken);
            try {
                AuthTokenDto loginData = mgmtAuthApi.loginWithToken(tokenAsJson);
                userID = loginData.getUserId();
                this.managementToken = loginData.getAccessToken();
            } catch (IllegalArgumentException e) {
                throw new ServerNotFoundException(e.getMessage(), stdMgmtURL);
            } catch (net.ssehub.studentmgmt.backend_api.ApiException e) {
                throw new UnknownCredentialsException("Could not login \"" + userName
                    + "\", credentials are unknown. Please check that user exist.");
            }
        }
        
        return userID != null;
    }

    /**
     * Returns the user name of the user.
     * @return The user name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the token to be used to query the <b>Student Management Service</b>.
     * @return The token of the <b>Student Management Service</b>.
     */
    public String getManagementToken() {
        return managementToken;
    }

    /**
     * The ID of the user, used by the <b>student management system</b>.
     * @return the userID.
     */
    public String getUserID() {
        return userID;
    }
}
