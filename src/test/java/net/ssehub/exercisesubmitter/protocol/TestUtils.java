package net.ssehub.exercisesubmitter.protocol;

import net.ssehub.exercisesubmitter.protocol.backend.LoginComponentIntegrationTests;

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
}
