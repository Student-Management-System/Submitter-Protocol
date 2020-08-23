package net.ssehub.exercisesubmitter.protocol.frontend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;

/**
 * This class declares <b>integration</b> tests for the {@link SubmissionHookProtocol} class.
 * These tests communicates with the REST test server.
 * @author Kunold
 *
 */
public class SubmissionHookProtocolIntegrationTest {
    
    /**
     * Test if {link {@link SubmissionHookProtocol#getAssignmentByName(String)} returns the specified assignment.
     */
    @Test
    public void testGetAssignmentByName() throws NetworkException {
        String expectedAssignment = "Test_Assignment 01 (Java)";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        Assertions.assertNotNull(assignment);
        Assertions.assertEquals(expectedAssignment, assignment.getName());
    }
    
    /**
     * Creates an {@link SubmissionHookProtocol} with default settings and logs in a tutor.
     * Useful for most tests.
     * @return {@link SubmissionHookProtocol} usable for testing.
     */
    private SubmissionHookProtocol initProtocol() {
        // Init protocol
        SubmissionHookProtocol protocol = new SubmissionHookProtocol(TestUtils.TEST_AUTH_SERVER,
            TestUtils.TEST_MANAGEMENT_SERVER, TestUtils.TEST_DEFAULT_JAVA_COURSE, TestUtils.TEST_SUBMISSION_SERVER);
        protocol.setSemester(TestUtils.TEST_DEFAULT_SEMESTER);
        String[] credentials = TestUtils.retreiveCredentialsFormVmArgs();
        try {
            protocol.login(credentials[0], credentials[1]);
        } catch (NetworkException e) {
            Assertions.fail("Could not login as tutor/lecturor", e);
        }
        return protocol;
    }

}
