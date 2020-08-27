package net.ssehub.exercisesubmitter.protocol.frontend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;
import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto.SeverityEnum;

/**
 * This class declares <b>integration</b> tests for the {@link SubmissionHookProtocol} class.
 * These tests communicates with the REST test server.
 * @author El-Sharkawy
 * @author Kunold
 *
 */
public class SubmissionHookProtocolIntegrationTest {
    private ReviewerProtocol protocol;
    private Assessment assessment;
    
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
     * Tests that {@link SubmissionHookProtocol#submitAssessment(Assignment, Assessment)} can submit
     * an {@link Assessment}. Parameters:
     * <ul>
     *   <li>Single assignment</li>
     *   <li>New assessment</li>
     *   <li>No partials</li>
     * </ul>
     */
    @Test
    public void testSubmitAssessmentNewAssessmentUser() throws NetworkException {
        String expectedAssignment = "Test_Assignment 03 (Java) - SINGLE - IN_REVIEW";
        String group = "elshar";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.IN_REVIEW, TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_SINGLE);
        // Marks this.this.assessment for removal via the cleanUp-Method
        protocol = hook.getProtocol();
        
        // Test that assessment does not exist on server
        Assessment assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(assessment, false);
        
        // Modify assessment
        assessment.setAchievedPoints(42);
        
        // Upload assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Test that assessment does now exist on server
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#submitAssessment(Assignment, Assessment)} can submit
     * an {@link Assessment}. Parameters:
     * <ul>
     *   <li>Group assignment</li>
     *   <li>New assessment</li>
     *   <li>No partials</li>
     * </ul>
     */
    @Test
    public void testSubmitAssessmentNewAssessmentGroup() throws NetworkException {
        String expectedAssignment = "Test_Assignment 08 (Java) - GROUP - IN_REVIEW";
        String group = "Testgroup 3";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.IN_REVIEW, TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_GROUP);
        // Marks this.this.assessment for removal via the cleanUp-Method
        protocol = hook.getProtocol();
        
        // Test that assessment does not exist on server
        Assessment assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(assessment, false);
        
        // Modify assessment
        assessment.setAchievedPoints(10);
        
        // Upload assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Test that assessment does now exist on server
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#submitAssessment(Assignment, Assessment)} can submit
     * an {@link Assessment}. Parameters:
     * <ul>
     *   <li>Group assignment</li>
     *   <li>Update of an existing assessment</li>
     *   <li>No partials</li>
     * </ul>
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
     * Tests that {@link SubmissionHookProtocol#submitAssessment(Assignment, Assessment)} can submit
     * an {@link Assessment}. Parameters:
     * <ul>
     *   <li>Group assignment</li>
     *   <li>New assessment</li>
     *   <li>New partials</li>
     * </ul>
     */
    @Test
    public void testSubmitAssessmentNewPartials() throws NetworkException {
        String expectedAssignment = "Test_Assignment 08 (Java) - GROUP - IN_REVIEW";
        String group = "Testgroup 3";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.IN_REVIEW, TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_GROUP);
        // Marks this.this.assessment for removal via the cleanUp-Method
        protocol = hook.getProtocol();
        
        // Test that assessment does not exist on server
        Assessment assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(assessment, false);
        
        // Modify assessment
        assessment.setAchievedPoints(10);
        String tool = "Compiler";
        String severity = SeverityEnum.ERROR.name();
        String description = "Classes do not compile";
        String file = "File.java";
        Integer line = 42;
        assessment.addAutomaticReview(tool, severity, description, file, line);
        
        // Upload assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Test that assessment does now exist on server -> Read from server
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
        Assertions.assertEquals(1, this.assessment.partialAsssesmentSize());
        
        // Test partial assessment
        PartialAssessmentDto partial = this.assessment.getPartialAssessment(0);
        Assertions.assertNotNull(partial.getId());
        Assertions.assertEquals(tool, partial.getTitle());
        Assertions.assertEquals(severity, partial.getSeverity().name());
        Assertions.assertEquals(file, partial.getPath());
        Assertions.assertEquals(line.intValue(), partial.getLine().intValue());
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#submitAssessment(Assignment, Assessment)} can submit
     * an {@link Assessment}. Parameters:
     * <ul>
     *   <li>Group assignment</li>
     *   <li>Modify assessment</li>
     *   <li>Modify partial</li>
     * </ul>
     */
    @Test
    public void testSubmitAssessmentModifyPartial() throws NetworkException {
        String expectedAssignment = "Test_Assignment 08 (Java) - GROUP - IN_REVIEW";
        String group = "Testgroup 3";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.IN_REVIEW, TestUtils.TEST_DEFAULT_REVIEWABLE_ASSIGNMENT_GROUP);
        // Marks this.this.assessment for removal via the cleanUp-Method
        protocol = hook.getProtocol();
        
        // Test that assessment does not exist on server
        Assessment assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(assessment, false);
        
        // Modify assessment
        assessment.setAchievedPoints(10);
        String tool = "Compiler";
        String severity = SeverityEnum.ERROR.name();
        String description = "Classes do not compile";
        assessment.addAutomaticReview(tool, severity, description, "File.java", 42);
        
        // Upload assessment: Basis for the test!
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Read assessment from server and modify the partial
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
        Assertions.assertEquals(1, this.assessment.partialAsssesmentSize());
        Assertions.assertNotNull(this.assessment.getPartialAssessment(0).getId());
        this.assessment.clearPartialAssessments();
        String file = "Another_File.java";
        Integer line = 21;
        this.assessment.addAutomaticReview(tool, severity, description, file, line);
        
        // Upload modified assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, this.assessment));
        
        // Test that partial was modified
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
        Assertions.assertEquals(1, this.assessment.partialAsssesmentSize());
        
        // Test partial assessment
        PartialAssessmentDto partial = this.assessment.getPartialAssessment(0);
        Assertions.assertNotNull(partial.getId());
        Assertions.assertEquals(tool, partial.getTitle());
        Assertions.assertEquals(severity, partial.getSeverity().name());
        Assertions.assertEquals(file, partial.getPath());
        Assertions.assertEquals(line.intValue(), partial.getLine().intValue());
    }
    
    /**
     * Cleans up temporarily created objects if necessary.
     * Requires that the protocol and the newly created {@link Assessment} was saved during the test.
     * This is done outside of the test to ensure deletion even if tests stops during its execution.
     */
    @AfterEach
    public void cleanup() {
        if (null != protocol && null != assessment) {
            try {
                protocol.deleteAssessment(assessment.getAssignmentID(), assessment.getAssessmentID());
            } catch (NetworkException e) {
                Assertions.fail("Could not delete newly created assessment due to " + e.getMessage());
            }
        }
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
