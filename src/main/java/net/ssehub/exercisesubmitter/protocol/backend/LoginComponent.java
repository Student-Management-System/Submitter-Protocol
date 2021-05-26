package net.ssehub.exercisesubmitter.protocol.backend;

import java.net.ConnectException;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.common.security.GuardedString.Accessor;

import net.ssehub.studentmgmt.backend_api.ApiClient;
import net.ssehub.studentmgmt.backend_api.api.AuthenticationApi;
import net.ssehub.studentmgmt.backend_api.model.UserDto;
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
    private UserDto user;
    
    // Credentials to allow re-login after time out
    private String loginUser;
    private GuardedString loginPasswort;
    
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
        user = null;
        
        // Save credentials for re-login
        loginUser = userName;
        if (null != password) {
            loginPasswort = new GuardedString(password.toCharArray());
        }
        
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
            try {
                mgmtAuthApi.getApiClient().setAccessToken(tmpToken);
                user = mgmtAuthApi.whoAmI();
                
                if (null != user) {
                    managementToken = tmpToken;
                }
            } catch (IllegalArgumentException e) {
                throw new ServerNotFoundException(e.getMessage(), stdMgmtURL);
            } catch (net.ssehub.studentmgmt.backend_api.ApiException e) {
                throw new UnknownCredentialsException("Could not login \"" + userName
                    + "\", credentials are unknown. Please check that user exist.");
            }
        }
        
        return user != null;
    }
    
    /**
     * Provides an automatic re-login after the session has been expired and only if the user was successfully logged
     * in before.
     * @return The new token for the student management server or <tt>null</tt> if this action was not successful.
     *     In this case, no second re-login will be possible.
     * @throws UnknownCredentialsException If the credentials are wrong or the user is unknown by the system.
     * @throws ServerNotFoundException If one of the two servers is unreachable by the specified URLs.
     */
    public String reLogin() throws ServerNotFoundException, UnknownCredentialsException {
        String newToken = null;
        
        // Apply re-login only if user was already successfully logged in
        if (null != user.getId()) {
            String usedPW = null;    
            if (null != loginPasswort) {
                final StringBuffer pw = new StringBuffer();
                loginPasswort.access(new Accessor() {
                    
                    @Override
                    public void access(char[] clearChars) {
                        pw.append(clearChars);
                        
                    }
                });
                usedPW = pw.toString();
            }
            
            boolean success;
            try {
                success = login(loginUser, usedPW);
            } catch (UnknownCredentialsException e) {
                // Avoid automatic re-login before re-throwing the exception
                user = null;
                throw e;
            }
            if (success) {
                newToken = managementToken;
            } else {
                // Avoid automatic re-login
                user = null;
            }
        }
        
        return newToken;
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
        return user.getId();
    }
}
