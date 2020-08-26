package net.ssehub.exercisesubmitter.protocol.frontend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;
import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;

/**
 * This class declares <b>integration</b> tests for the {@link SubmissionHookProtocol} class.
 * These tests communicates with the REST test server.
 * @author El-Sharkawy
 * @author Kunold
 *
 */
public class SubmissionHookProtocolIntegrationTest {
    
    /**
     * Test if {link {@link SubmissionHookProtocol#getAssignmentByName(String)} returns the specified assignment.
     */
    @Test
    public void testGetAssignmentByName() throws NetworkException {
        String expectedAssignment = "Test_Assignment 06 (Java) Testat In Progress";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.SUBMISSION, TestUtils.TEST_DEFAULT_SUBMITABLE_ASSIGNMENT_SINGLE);
        Assertions.assertEquals(expectedAssignment, assignment.getName());
    }
    
    /**
     * Test if {link {@link SubmissionHookProtocol#getAssignmentByName(String)} returns the specified assignment.
     */
    @Test
    public void testGetAssignmentByNameThrowException() throws NetworkException {
        String expectedAssignment = "Non existent Assignment";
        
        SubmissionHookProtocol hook = initProtocol();
        try {
            hook.getAssignmentByName(expectedAssignment);
            Assertions.fail("Expected to fail since assignment does not exist");
        } catch (DataNotFoundException e) {
            Assertions.assertEquals(DataType.ASSIGNMENTS_NOT_FOUND, e.getType());
        }
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#getAssignmentByName(String)}. With the following parameters:
     * <ul>
     *   <li><b>Submission state:</b> <tt>IN_PROGRESS</tt> (while students submit)</li>
     *   <li><b>Assessment state:</b> No assessment on server so far</li>
     *   <li><b>Submitter:</b> an existent user (for a single submission)</li>
     * </ul>
     */
    @Test
    public void testLoadAssessmentByNameCreateDuringSubmission() throws NetworkException {
        String expectedAssignment = "Test_Assignment 06 (Java) Testat In Progress";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.SUBMISSION, TestUtils.TEST_DEFAULT_SUBMITABLE_ASSIGNMENT_SINGLE);
        
        Assessment assessment = hook.loadAssessmentByName(assignment, TestUtils.TEST_USERS_OF_JAVA[0]);
        assertAssessment(assessment, false);
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#getAssignmentByName(String)}. With the following parameters:
     * <ul>
     *   <li><b>Submission state:</b> <tt>IN_REVIEW</tt></li>
     *   <li><b>Assessment state:</b> Existing Assessment</li>
     *   <li><b>Submitter:</b> an existent user (for a single submission)</li>
     * </ul>
     */
    @Test
    public void testLoadAssessmentByNameExistingAssessment() throws NetworkException {
        String expectedAssignment = "Test_Assignment 03 (Java) - SINGLE - IN_REVIEW";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.IN_REVIEW, TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_SINGLE);
        
        Assessment assessment = hook.loadAssessmentByName(assignment, "mmustermann");
        assertAssessment(assessment, true);
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#getAssignmentByName(String)}. With the following parameters:
     * <ul>
     *   <li><b>Submission state:</b> <tt>IN_REVIEW</tt></li>
     *   <li><b>Assessment state:</b> No assessment on server so far</li>
     *   <li><b>Submitter:</b> an existent user (for a single submission)</li>
     * </ul>
     */
    @Test
    public void testLoadAssessmentByNameCreateDuringReview() throws NetworkException {
        String expectedAssignment = "Test_Assignment 03 (Java) - SINGLE - IN_REVIEW";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.IN_REVIEW, TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_SINGLE);
        
        Assessment assessment = hook.loadAssessmentByName(assignment, TestUtils.TEST_USERS_OF_JAVA[1]);
        assertAssessment(assessment, false);
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#getAssignmentByName(String)}. With the following parameters:
     * <ul>
     *   <li><b>Submission state:</b> <tt>IN_PROGRESS</tt> (while students submit)</li>
     *   <li><b>Assessment state:</b> No assessment on server so far</li>
     *   <li><b>Submitter:</b> an <b>invalid</b> existent user</li>
     * </ul>
     */
    @Test
    public void testLoadAssessmentByNameCreateDuringSubmissionInvalidUser() throws NetworkException {
        String expectedAssignment = "Test_Assignment 06 (Java) Testat In Progress";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.SUBMISSION, TestUtils.TEST_DEFAULT_SUBMITABLE_ASSIGNMENT_SINGLE);
        
        try {
            hook.loadAssessmentByName(assignment, "A non existent user");
            Assertions.fail("Expected to fail since user does not exist");
        } catch (DataNotFoundException e) {
            Assertions.assertEquals(DataType.USER_NOT_FOUND, e.getType());
        }
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#getAssignmentByName(String)}. With the following parameters:
     * <ul>
     *   <li><b>Submission state:</b> <tt>IN_PROGRESS</tt> (while students submit)</li>
     *   <li><b>Assessment state:</b> No assessment on server so far</li>
     *   <li><b>Submitter:</b> an existent group (for a group submission)</li>
     * </ul>
     */
    @Test
    public void testLoadAssessmentByNameCreateDuringSubmissionGroup() throws NetworkException {
        String expectedAssignment = "Test_Assignment 01 (Java)";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.SUBMISSION, TestUtils.TEST_DEFAULT_SUBMITABLE_ASSIGNMENT_GROUP);
        
        Assessment assessment = hook.loadAssessmentByName(assignment, "Testgroup 1");
        assertAssessment(assessment, false);
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#getAssignmentByName(String)}. With the following parameters:
     * <ul>
     *   <li><b>Submission state:</b> <tt>IN_PROGRESS</tt> (while students submit)</li>
     *   <li><b>Assessment state:</b> No assessment on server so far</li>
     *   <li><b>Submitter:</b> an <b>invalid</b> existent user</li>
     * </ul>
     */
    @Test
    public void testLoadAssessmentByNameCreateDuringSubmissionInvalidGroup() throws NetworkException {
        String expectedAssignment = "Test_Assignment 01 (Java)";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.SUBMISSION, TestUtils.TEST_DEFAULT_SUBMITABLE_ASSIGNMENT_GROUP);
        
        try {
            hook.loadAssessmentByName(assignment, "A non existent group");
            Assertions.fail("Expected to fail since group does not exist");
        } catch (DataNotFoundException e) {
            Assertions.assertEquals(DataType.GROUP_NOT_FOUND, e.getType());
        }
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#getAssignmentByName(String)}. With the following parameters:
     * <ul>
     *   <li><b>Submission state:</b> <tt>IN_REVIEW</tt></li>
     *   <li><b>Assessment state:</b> Existing Assessment</li>
     *   <li><b>Submitter:</b> an existent group (for a group submission)</li>
     * </ul>
     */
    @Test
    public void testLoadAssessmentByNameExistingAssessmentGroup() throws NetworkException {
        String expectedAssignment = "Test_Assignment 08 (Java) - GROUP - IN_REVIEW";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.IN_REVIEW, TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_GROUP);
        
        Assessment assessment = hook.loadAssessmentByName(assignment, "Testgroup 1");
        assertAssessment(assessment, true);
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#submitAssessment(Assignment, Assessment)} can create and submit
     * a <b>new</b> {@link Assessment}.
     */
    @Test
    public void testSubmitAssessmentNewAssessment() throws NetworkException {
        String expectedAssignment = "Test_Assignment 08 (Java) - GROUP - IN_REVIEW";
        String group = "Testgroup 3";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.IN_REVIEW, TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_GROUP);
        ReviewerProtocol rp = hook.getProtocol();
        
        // Test that assessment does not exist on server
        Assessment assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(assessment, false);
        
        // Modify assessment
        assessment.setAchievedPoints(10);
        
        // Upload assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Test that assessment does now exist on server
        assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(assessment, true);
        
        // All good, clean up -> remove newly created assessment
        rp.deleteAssessment(assessment.getAssignmentID(), assessment.getAssessmentID());
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#submitAssessment(Assignment, Assessment)} can modify and submit
     * an <b>existent</b> {@link Assessment}.
     */
    @Test
    public void testSubmitAssessmentExistentAssessment() throws NetworkException {
        String expectedAssignment = "Test_Assignment 08 (Java) - GROUP - IN_REVIEW";
        String group = "Testgroup 1";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.IN_REVIEW, TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_GROUP);
        
        // Test that assessment does not exist on server
        Assessment assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(assessment, true);
        
        // Modify assessment (will change values on server, however exact value is never used in test cases)
        double points = (assessment.getAchievedPoints() + 1) % assignment.getPoints();
        assessment.setAchievedPoints(points);
        
        // Upload assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Test that assessment does now exist on server
        assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(assessment, true);
        Assertions.assertEquals(points, assessment.getAchievedPoints());
    }
    
    /**
     * Asserts an {@link Assessment}.
     * @param assessment The assessment to test.
     * @param expectedOnServer <tt>true</tt> expected that assessment exists on server, <tt>false</tt> a new one shall
     *     be created on the fly.
     */
    private static void assertAssessment(Assessment assessment, boolean expectedOnServer) {
        Assertions.assertNotNull(assessment);
        if (expectedOnServer) {
            Assertions.assertNotNull(assessment.getAssessmentID());
        } else {
            Assertions.assertNull(assessment.getAssessmentID());
        }
    }
    
    /**
     * Asserts an {@link Assignment}.
     * @param assignment An assignment returned via {@link SubmissionHookProtocol#getAssignmentByName(String)}.
     * @param expectedState The expected state of the assignment.
     * @param expectedID Optional: The expected ID at the server.
     */
    private static void assertAssignment(Assignment assignment, State expectedState, String expectedID) {
        Assertions.assertNotNull(assignment);
        Assertions.assertEquals(expectedState, assignment.getState());
        if (null != expectedID) {
            Assertions.assertEquals(expectedID, assignment.getID());
        }
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
