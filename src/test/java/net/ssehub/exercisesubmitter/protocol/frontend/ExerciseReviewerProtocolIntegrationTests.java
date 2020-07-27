package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.ArrayList;
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
            .filter(a -> a.getID().equals(TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_SINGLE))
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
     * Method should get an assessment for the given user name.
     * @throws NetworkException If server, which is used for the integration test,
     *     can not be reached through a network error or miss-configuration.
     */
    @Test
    public void testGetAssessmentForSubmissionSingle() throws NetworkException {
        String reviewedUser = "mmustermann";
        
        // Init protocol
        ExerciseReviewerProtocol reviewer = initReviewer();
        
        // Load assignment, which is used to retrieve available reviews
        Assignment assignment = reviewer.getReviewableAssignments().stream()
            .filter(a -> a.getID().equals(TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_SINGLE))
            .findFirst()
            .orElse(null);
        Assertions.assertNotNull(assignment);
        
        // Test postcondition: Retrieve reviews for the user
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
     * Tests {@link ExerciseReviewerProtocol#getAssessmentForSubmission(String)}.
     * Method should get an assessment for the given group name, which has no assignment on the server so far.
     * @throws NetworkException If server, which is used for the integration test,
     *     can not be reached through a network error or miss-configuration.
     */
    @Test
    public void testGetAssessmentForSubmissionGroupCreateOnTheFly() throws NetworkException {
        String reviewedGroup = "Testgroup 3";
        
        // Init protocol
        ExerciseReviewerProtocol reviewer = initReviewer();
        
        // Load assignment, which is used to retrieve available reviews
        Assignment assignment = reviewer.getReviewableAssignments().stream()
            .filter(a -> a.getID().equals(TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_GROUP))
            .findFirst()
            .orElse(null);
        Assertions.assertNotNull(assignment);
        
        // Test postcondition: Retrieve reviews for the user
        reviewer.loadAssessments(assignment);
        try {
            Assessment assessment = reviewer.getAssessmentForSubmission(reviewedGroup);
            Assertions.assertNotNull(assessment);
            Assertions.assertEquals(reviewedGroup, assessment.getSubmitterName());
            User[] expected = new User[] {new User("elshar", "elshar"), new User("kunold", "kunold")};
            List<User> actual = new ArrayList<>();
            for (User user : assessment) {
                actual.add(user);
            }
            Assertions.assertArrayEquals(expected, actual.toArray());
        } catch (NetworkException e) {
            Assertions.fail("Unexpected exception: Method should return an assessment stub for review", e);
        }
    }
    
    /**
     * Tests that {@link ExerciseReviewerProtocol#submitAssessment(Assessment)} can update an existing assessment on the
     * server.
     */
    @Test
    public void testSubmitAssessmentUpdate() throws NetworkException {
        String reviewedUser = "mmustermann";
        
        // Init protocol
        ExerciseReviewerProtocol reviewer = initReviewer();
        
        // Load assignment, which is used to retrieve available reviews
        Assignment assignment = reviewer.getReviewableAssignments().stream()
            .filter(a -> a.getID().equals(TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_SINGLE))
            .findFirst()
            .orElse(null);
        Assertions.assertNotNull(assignment);
        
        // Edit existing assessment
        reviewer.loadAssessments(assignment);
        Assessment assessment = reviewer.getAssessmentForSubmission(reviewedUser);
        Assertions.assertNotNull(assessment);
        double points = assessment.getAchievedPoints();
        // We need anything new to check and we need to avoid buffer overflow
        double newPoints = (points % assignment.getPoints()) + 1;
        Assertions.assertNotEquals(points, newPoints);
        assessment.setAchievedPoints(newPoints);
        reviewer.submitAssessment(assessment);
        
        // Test: Assignment should have changed points on server (avoid cache of reviewer by creating a new instance)
        reviewer = initReviewer();
        Assertions.assertTrue(reviewer.getAssessments().isEmpty());
        reviewer.loadAssessments(assignment);
        assessment = reviewer.getAssessmentForSubmission(reviewedUser);
        Assertions.assertEquals(newPoints, assessment.getAchievedPoints());
    }
    
    /**
     * Creates an {@link ExerciseReviewerProtocol} with default settings and logs in a tutor.
     * Useful for most tests.
     * @return {@link ExerciseReviewerProtocol} usable for testing.
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
