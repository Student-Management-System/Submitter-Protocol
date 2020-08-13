package net.ssehub.exercisesubmitter.protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;

import net.ssehub.exercisesubmitter.protocol.backend.LoginComponent;
import net.ssehub.exercisesubmitter.protocol.backend.LoginComponentIntegrationTests;
import net.ssehub.exercisesubmitter.protocol.backend.ServerNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.UnknownCredentialsException;

/**
 * Provides constants and utility functions used by multiple tests.
 * @author El-Sharkawy
 *
 */
public class TestUtils {
    
    // Test parameters
    public static final String TEST_AUTH_SERVER = "http://147.172.178.30:8080";
    public static final String TEST_MANAGEMENT_SERVER = "http://147.172.178.30:3000";
    public static final String TEST_SUBMISSION_SERVER = "http://svn.submission.fake/not_existing_submissions";
    
    /**
     * The default java course used for integration tests.
     * Other course related information stored in this class are related to this course.
     */
    public static final String TEST_DEFAULT_JAVA_COURSE = "java";
    public static final String TEST_DEFAULT_SEMESTER = "wise1920";
    public static final String TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_SINGLE = "993b3cd0-6207-11ea-bc55-0242ac130003";
    public static final String TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_GROUP = "f50b8474-1fb9-4d69-85a2-76648d0fd3f9";

    /**
     * Extracts user name and password from the VM args and aborts the test if they are not provided.
     * @return <tt>[userName, password]</tt>
     */
    public static String[] retreiveCredentialsFormVmArgs() {
        String userName = System.getProperty("test_user");
        String pw = System.getProperty("test_password");
        LoginComponentIntegrationTests.assumeSpecified(userName, "test_user", "user name");
        LoginComponentIntegrationTests.assumeSpecified(pw, "test_password", "password");
        
        return new String[] {userName, pw};
    }
    
    /**
     * Extracts credentials provided via VM arguments and logs in the user.
     * Useful to test authorized API calls.
     * @return The access token to access authorized API calls of the student management system.
     * @see #retreiveCredentialsFormVmArgs()
     */
    public static String retreiveAccessToken() {
        String[] credentials = retreiveCredentialsFormVmArgs();
        LoginComponent loginComp = new LoginComponent(TEST_AUTH_SERVER, TEST_MANAGEMENT_SERVER);
        try {
            Assumptions.assumeTrue(loginComp.login(credentials[0], credentials[1]));
        } catch (UnknownCredentialsException e) {
            Assertions.fail("Could not login due to unknown credentials: " + e.getMessage());
        } catch (ServerNotFoundException e) {
            Assertions.fail("Could not login due to unknown server specified: " + e.getMessage());
        }
        
        return loginComp.getManagementToken();
    }
}
