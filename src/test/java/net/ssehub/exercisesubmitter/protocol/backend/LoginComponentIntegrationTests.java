package net.ssehub.exercisesubmitter.protocol.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
import net.ssehub.studentmgmt.backend_api.ApiClient;
import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.UsersApi;

/**
 * This class declares <b>integration</b> tests for the {@link LoginComponent} class.
 * These tests communicates with the REST test server.
 * 
 * @author El-Sharkawy
 *
 */
public class LoginComponentIntegrationTests {
    
    /**
     * Create a {@link LoginComponent}, which may be used during testing.
     * If no credentials are provided, test will be skipped (and marked yellow in Jenkins).
     * Required properties are:
     * <ul>
     *   <li>test_user</li>
     *   <li>test_password</li>
     * </ul>
     * 
     * @return A logged in user.
     */
    public static LoginComponent createLoginForTests() {
        String[] credentials = TestUtils.retreiveCredentialsFormVmArgs();
        LoginComponent login = new LoginComponent(TestUtils.TEST_AUTH_SERVER, TestUtils.TEST_MANAGEMENT_SERVER);
        try {
            login.login(credentials[0], credentials[1]);
        } catch (UnknownCredentialsException e) {
            Assertions.fail(e);
        } catch (ServerNotFoundException e) {
            Assertions.fail(e);
        }
        
        return login;
    }
    
    /**
     * Tests precondition if credentials have been provided via external properties (to avoid placing valid credentials
     * inside the repository). If no credentials are provided, test will be skipped (and marked yellow in Jenkins).
     * Required properties are:
     * <ul>
     *   <li>test_user</li>
     *   <li>test_password</li>
     * </ul>
     * 
     * @param variable The variable containing the properties value
     * @param propertyName the name of the properties (one from above)
     * @param typeName The name of the properties (human readable name)
     */
    public static void assumeSpecified(String variable, String propertyName, String typeName) {
        Assumptions.assumeTrue(variable != null && !variable.isEmpty(), "No " + typeName + " specified, please specify "
            + "a test " + typeName + " via the property \"" + propertyName + "\", either on the command line via "
            + "-D" + propertyName + "= or on in maven/Eclipse via specifying system properties.");        
    }
    
    /**
     * Tests the integration of three systems to login a user.
     * The involved systems are:
     * <ul>
     *   <li>Authentication System (aka. Sparky Service)</li>
     *   <li>Student Management System</li>
     *   <li>Submitter Protocol</li>
     * </ul>
     * @throws UnknownCredentialsException If non valid credentials have been used (not tested here)
     * @throws ServerNotFoundException If one of the 2 other servers are not found, this may happen if hard coded
     *     URLs change
     */
    @Test
    public void testLogin() throws UnknownCredentialsException, ServerNotFoundException {
        String userName = System.getProperty("test_user");
        String pw = System.getProperty("test_password");
        
        // Check if credentials have been specified via system properties
        assumeSpecified(userName, "test_user", "user name");
        assumeSpecified(pw, "test_password", "password");
        
        // Perform login
        LoginComponent loginComp = new LoginComponent(TestUtils.TEST_AUTH_SERVER, TestUtils.TEST_MANAGEMENT_SERVER);
        boolean success = loginComp.login(userName, pw);
        
        // Test if login was successful
        Assertions.assertTrue(success, "Could not login. Check if valid credentials are used, otherwise "
            + LoginComponent.class.getSimpleName() + " is defect.");
        String id = loginComp.getUserID();
        Assertions.assertNotNull(id, "No userID retrieved: " + LoginComponent.class.getSimpleName() + " is defect.");
        Assertions.assertFalse(id.isEmpty(), "No userID retrieved: "
            + LoginComponent.class.getSimpleName() + " is defect.");
    }
    
    /**
     * Tests that generated Token can be used to query for protected services.
     * @throws UnknownCredentialsException If non valid credentials have been used (not tested here)
     * @throws ServerNotFoundException If one of the 2 other servers are not found, this may happen if hard coded
     *     URLs change
     */
    @Test
    public void testRetrievelOfValidAccessToken() throws UnknownCredentialsException, ServerNotFoundException {
        String testUserID = "abc6e1c0-6db0-4c35-8d97-07cc7173c34c";
        String userName = System.getProperty("test_user");
        String pw = System.getProperty("test_password");
        
        // Precondition: Check if credentials have been specified via system properties
        assumeSpecified(userName, "test_user", "user name");
        assumeSpecified(pw, "test_password", "password");
        
        // Precondition: Check that tested services requires valid token
        ApiClient client = new ApiClient();
        client.setBasePath(TestUtils.TEST_MANAGEMENT_SERVER);
        UsersApi userApi = new UsersApi(client);
        boolean exceptionOccured = false;
        try {
            userApi.getUserById(testUserID);
            Assertions.fail("Expected to fail if no Token is used. Possibly an unprotected sevices was queried.");
        } catch (ApiException e) {
            exceptionOccured = true;
        }
        Assertions.assertTrue(exceptionOccured, "Expected to fail if no Token is used. Possibly an unprotected sevices "
            + "was queried.");
        
        // Perform login
        LoginComponent loginComp = new LoginComponent(TestUtils.TEST_AUTH_SERVER, TestUtils.TEST_MANAGEMENT_SERVER);
        loginComp.login(userName, pw);
        
        //Query a service that requires valid token
        client.setAccessToken(loginComp.getManagementToken());
        try {
            userApi.getUserById(testUserID);
        } catch (ApiException e) {
            Assertions.fail("Service should not fail when used with valid token. reason: " + e.getMessage(), e);
        }
    }
}
