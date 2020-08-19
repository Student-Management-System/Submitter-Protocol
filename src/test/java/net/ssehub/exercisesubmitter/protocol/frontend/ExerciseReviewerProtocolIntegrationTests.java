package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;
import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;

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
     * Tests that {@link ExerciseReviewerProtocol#getReviewedAssignment()} get an assignment.
     * @throws NetworkException If server, which is used for the integration test,
     *     can not be reached through a network error or miss-configuration.
     */
    @Test
    public void testGetReviewedAssignment() throws NetworkException {
        // Init protocol
        ExerciseReviewerProtocol reviewer = initReviewer();
        
        // Test precondition: no assignment available
        Assignment assignment = reviewer.getReviewedAssignment();
        Assertions.assertNull(assignment);
        
        AssignmentDto dto = new AssignmentDto();
        dto.setName("reviewed Assignment");
        Assignment reviewedAssignment = new Assignment(dto);
        
        reviewer.loadAssessments(reviewedAssignment);
        
        assignment = reviewer.getReviewedAssignment();
        Assertions.assertNotNull(assignment);
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
            Set<User> expected = new HashSet<>();
            expected.add(new User("elshar", "elshar", "elshar@test.com"));
            expected.add(new User("kunold", "kunold", "kunold@test.com"));
            int nElements = 0;
            for (User user : assessment) {
                Assertions.assertTrue(expected.contains(user));
                nElements++;
            }
            Assertions.assertEquals(expected.size(), nElements);
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
     * Tests that {@link ExerciseReviewerProtocol#submitAssessment(Assessment)} can create a new existing assessment
     * on the server.
     */
    @Test
    public void testSubmitAssessmentCreate() throws NetworkException {
        String reviewedGroup = "Testgroup 3";
        
        // Init protocol
        ExerciseReviewerProtocol reviewer = initReviewer();
        ReviewerProtocol rp = reviewer.getProtocol();
        
        // Load assignment, which is used to retrieve available reviews
        Assignment assignment = reviewer.getReviewableAssignments().stream()
            .filter(a -> a.getID().equals(TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_GROUP))
            .findFirst()
            .orElse(null);
        Assertions.assertNotNull(assignment);
        
        // Create a new assessment
        reviewer.loadAssessments(assignment);
        Assessment assessment = reviewer.getAssessmentForSubmission(reviewedGroup);
        Assertions.assertNotNull(assessment);
        assessment.setAchievedPoints(assignment.getPoints());
        assessment.setFullReviewComment("Perfect solution");
        
        // New assessment must not have an ID, otherwise it exist already on server
        if (null != assessment.getAssessmentID()) {
            // Delete (a manually) submitted assessment and try it again
            rp.deleteAssessment(assessment.getAssignmentID(), assessment.getAssessmentID());
            reviewer.loadAssessments(assignment);
            assessment = reviewer.getAssessmentForSubmission(reviewedGroup);
            Assertions.assertNotNull(assessment);
            assessment.setAchievedPoints(assignment.getPoints());
            assessment.setFullReviewComment("Perfect solution");
        }
        Assertions.assertNull(assessment.getAssessmentID());
        
        // Double check that assignment does not exist on server
        AssessmentDto candiate = rp.getAssessments(assessment.getAssignmentID(), null).stream()
                .filter(a -> reviewedGroup.equals(a.getGroup().getName()))
                .findFirst()
                .orElse(null);
        Assertions.assertNull(candiate);
        
        // Upload new assessment
        reviewer.submitAssessment(assessment);
        
        // Test post condition: Assignment as an ID and exist on server
        Assertions.assertNotNull(assessment.getAssessmentID());
        candiate = rp.getAssessments(assessment.getAssignmentID(), null).stream()
                .filter(a -> reviewedGroup.equals(a.getGroup().getName()))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(candiate);
        Assertions.assertEquals(assessment.getAssessmentID(), candiate.getId());
        
        // All good, Clean up: Remove new assignment from server
        rp.deleteAssessment(assessment.getAssignmentID(), assessment.getAssessmentID());
    }
    
    /**
     * Tests that {@link ExerciseReviewerProtocol#loadParticipants()} loads all participating students.
     */
    @Test
    public void testLoadParticipants() throws NetworkException {
        ExerciseReviewerProtocol reviewer = initReviewer();
        List<User> participants = reviewer.loadParticipants();
        Assertions.assertNotNull(participants);
        Assertions.assertFalse(participants.isEmpty());
        Assertions.assertTrue(participants.contains(new User("Max Mustermann", "mmustermann",
            "max.mustermann@test.com")));
        Assertions.assertTrue(participants.contains(new User("Hans Peter", "hpeter", "hans.peter@test.com")));
        Assertions.assertTrue(participants.contains(new User("elshar", "elshar", "elshar@test.com")));
        Assertions.assertTrue(participants.contains(new User("kunold", "kunold", "kunold@test.com")));
    }
    
    /**
     * Test that {@link ExerciseReviewerProtocol#getPathToSubmission(Assignment, String)} gets the path to submission.
     */
    @Test
    public void testGetPathToSubmission() {
        ExerciseReviewerProtocol reviewer = initReviewer();
        Assignment assignment = new Assignment("test", "testID", State.IN_REVIEW , true);
        Assertions.assertNotNull(reviewer.getPathToSubmission(assignment, "submissionName"));
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
