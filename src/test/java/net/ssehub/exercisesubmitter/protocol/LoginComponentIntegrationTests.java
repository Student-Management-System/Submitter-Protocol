package net.ssehub.exercisesubmitter.protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * This class declares <b>integration</b> tests for the {@link LoginComponent} class.
 * These tests communicates with the REST test server.
 * 
 * @author El-Sharkawy
 *
 */
public class LoginComponentIntegrationTests {
    
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
    private static void assumeSpecified(String variable, String propertyName, String typeName) {
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
        LoginComponent loginComp = new LoginComponent("http://147.172.178.30:8080", "http://147.172.178.30:3000");
        boolean success = loginComp.login(userName, pw);
        
        // Test if login was successful
        Assertions.assertTrue(success, "Could not login. Check if valid credentials are used, otherwise "
            + LoginComponent.class.getSimpleName() + " is defect.");
        String id = loginComp.getUserID();
        Assertions.assertNotNull(id, "No userID retrieved: " + LoginComponent.class.getSimpleName() + " is defect.");
        Assertions.assertFalse(id.isEmpty(), "No userID retrieved: "
            + LoginComponent.class.getSimpleName() + " is defect.");
    }
}
