package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;

/**
 * Tests the {@link SubmitterProtocol} <b>with</b> querying the REST server.
 * @author El-Sharkawy
 *
 */
public class SubmitterProtocolIntegrationTests {
    
    /**
     * Test if {@link SubmitterProtocol#getOpenAssignments()} returns a list of open assignments.
     * @throws NetworkException Must not occur, is not tested and network is not used. If this occur, internal API of
     *     {@link SubmitterProtocol} has been changed.
     */
    @Test
    public void testGetOpenAssignments() throws NetworkException {
        // Test data
        String expectedExercise = "Test_Assignment 01 (Java)";
        SubmitterProtocol protocol = initProtocol();
        
        // Test
        List<Assignment> assignments = protocol.getOpenAssignments();
        Assertions.assertFalse(assignments.isEmpty());
        Assertions.assertEquals(expectedExercise, assignments.get(0).getName());
    }
    
    /**
     * Creates an {@link SubmitterProtocol} with default settings and logs in a tutor.
     * Useful for most tests.
     * @return {@link SubmitterProtocol} usable for testing.
     */
    private SubmitterProtocol initProtocol() {
        // Init protocol
        SubmitterProtocol protocol = new SubmitterProtocol(TestUtils.TEST_AUTH_SERVER,
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
