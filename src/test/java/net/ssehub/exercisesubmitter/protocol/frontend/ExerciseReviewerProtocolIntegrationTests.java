package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;

/**
 * Declares <b>integration</b> tests for the {@link ExerciseReviewerProtocol}.
 * Requires valid credentials for testing provided via VM arguments.
 * These credentials must belong to a <b>TUTOR</b> or <b>LECTURER</b> of the tested course.
 * If no credentials are provided, test will be skipped (and marked yellow in Jenkins).
 * Required properties are:
 * <ul>
 *   <li>test_user</li>
 *   <li>test_password</li>
 * </ul>
 * @author El-Sharkawy
 *
 */
public class ExerciseReviewerProtocolIntegrationTests {
    
    /**
     * Tests that {@link ExerciseReviewerProtocol#loadAssessments(Assignment)} retrieves the list of already
     * available assessments.
     * @throws NetworkException If server, which is used for the integration test,
     *     can not be reached through a network error or miss-configuration.
     */
    @Test
    public void testLoadAssessments() throws NetworkException {
        // Init protocol
        ExerciseReviewerProtocol reviewer = initReviewer();
        
        // Test precondition: No assessments available
        List<Assessment> assessments = reviewer.getAssessments();
        Assertions.assertTrue(assessments.isEmpty());
        
        // Load assignment, which is used to retrieve available reviews
        Assignment assignment = reviewer.getReviewableAssignments().stream()
            .filter(a -> a.getID().equals(TestUtils.TEST_DEFAULT_REVIEABLE_ASSIGNMENT))
            .findFirst()
            .orElse(null);
        Assertions.assertNotNull(assignment);
        
        // Test postcondition: Retrieve reviews for assignment, should not be empty
        reviewer.loadAssessments(assignment);
        assessments = reviewer.getAssessments();
        Assertions.assertFalse(assessments.isEmpty());
    }

    /**
     * Tests {@link ExerciseReviewerProtocol#getAssessmentForSubmission(String)}.
     * Method should get an assessment for the given user/group name.
     * @throws NetworkException If server, which is used for the integration test,
     *     can not be reached through a network error or miss-configuration.
     */
    @Test
    public void testGetAssessmentForSubmission() throws NetworkException {
        String reviewedUser = "mmustermann";
        
        // Init protocol
        ExerciseReviewerProtocol reviewer = initReviewer();
        
        // Load assignment, which is used to retrieve available reviews
        Assignment assignment = reviewer.getReviewableAssignments().stream()
            .filter(a -> a.getID().equals(TestUtils.TEST_DEFAULT_REVIEABLE_ASSIGNMENT))
            .findFirst()
            .orElse(null);
        Assertions.assertNotNull(assignment);
        
        // Test postcondition: Retrieve reviews for assignment, should not be empty
        reviewer.loadAssessments(assignment);
        try {
            Assessment assessment = reviewer.getAssessmentForSubmission(reviewedUser);
            Assertions.assertNotNull(assessment);
            Assertions.assertEquals(reviewedUser, assessment.getSubmitterName());
            for (User user : assessment) {
                // Single exercise -> should have exactly one user
                Assertions.assertEquals(reviewedUser, user.getAccountName());                
            }
        } catch (NetworkException e) {
            Assertions.fail("Unexpected exception: Method should return an assessment stub for review", e);
        }
    }
    
    /**
     * Creates an {@link ExerciseReviewerProtocol} with default settings and logs in a tutor.
     * Useful for most tests.
     * @return {@link ExerciseReviewerProtocol} usuable for testing.
     */
    private ExerciseReviewerProtocol initReviewer() {
        // Init protocol
        ExerciseReviewerProtocol reviewer = new ExerciseReviewerProtocol(TestUtils.TEST_AUTH_SERVER,
            TestUtils.TEST_MANAGEMENT_SERVER, TestUtils.TEST_DEFAULT_JAVA_COURSE, TestUtils.TEST_SUBMISSION_SERVER);
        reviewer.setSemester(TestUtils.TEST_DEFAULT_SEMESTER);
        String[] credentials = TestUtils.retreiveCredentialsFormVmArgs();
        try {
            reviewer.login(credentials[0], credentials[1]);
        } catch (NetworkException e) {
            Assertions.fail("Could not login as tutor/lecturor", e);
        }
        return reviewer;
    }
}
