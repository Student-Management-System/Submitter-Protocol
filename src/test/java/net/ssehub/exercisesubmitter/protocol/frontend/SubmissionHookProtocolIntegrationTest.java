package net.ssehub.exercisesubmitter.protocol.frontend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;
import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.studentmgmt.backend_api.model.MarkerDto.SeverityEnum;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;

/**
 * This class declares <b>integration</b> tests for the {@link SubmissionHookProtocol} class.
 * These tests communicates with the REST test server.<br/>
 * Used ordered tests: Tests are <b>not</b> dependent or each other.
 * However, order should simplify synopsis, if tests crash because later tests assume that previous tests were
 * successful.<p>
 * This means that first tests test basic functionality and later tests test logically dependent and more complex
 * functionality. As a consequence, the first tests that fail should be fixed fist.
 * @author El-Sharkawy
 * @author Kunold
 *
 */
@TestMethodOrder(OrderAnnotation.class)
public class SubmissionHookProtocolIntegrationTest {
    private ReviewerProtocol protocol;
    private Assessment assessment;
    
    /**
     * Test if {link {@link SubmissionHookProtocol#getAssignmentByName(String)} returns the specified assignment.
     */
    @Order(1)
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
    @Order(2)
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
     * Tests that {@link SubmissionHookProtocol#createAssessment(Assignment, String)} throws and exception if an
     * {@link Assessment} shall be created for a group and an user-{@link Assignment}.
     * @throws NetworkException
     */
    @Order(3)
    @Test
    public void testCreateAssessmentInvalidUser() throws NetworkException {
        // User assignment
        String expectedAssignment = "Test_Assignment 06 (Java) Testat In Progress";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        
        try {
            hook.createAssessment(assignment, "Testgroup 1");
            Assertions.fail("Expected to fail since user assessment should be created for a group assignment");
        } catch (DataNotFoundException e) {
            Assertions.assertEquals(DataType.USER_NOT_FOUND, e.getType());
        }
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#createAssessment(Assignment, String)} throws and exception if an
     * {@link Assessment} shall be created for an user and a group-{@link Assignment}.
     * @throws NetworkException
     */
    @Order(3)
    @Test
    public void testCreateAssessmentInvalidGroup() throws NetworkException {
        // User assignment
        String expectedAssignment = "Test_Assignment 08 (Java) - GROUP - IN_REVIEW";
        
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        
        try {
            hook.createAssessment(assignment, TestUtils.TEST_USERS_OF_JAVA[3]);
            Assertions.fail("Expected to fail since group assessment should be created for an user assignment");
        } catch (DataNotFoundException e) {
            Assertions.assertEquals(DataType.GROUP_NOT_FOUND, e.getType());
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
    @Order(10)
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
    @Order(10)
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
    @Order(10)
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
    @Order(10)
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
    @Order(20)
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
    @Order(20)
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
    @Order(20)
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
    @Order(30)
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
    @Order(30)
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
    @Order(30)
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
        double points = ((assessment.getAchievedPoints() + 1) % assignment.getPoints());
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
     *   <li>Update of an existing assessment: Delete points during re-correction</li>
     *   <li>No partials</li>
     * </ul>
     */
    @Order(31)
    @Test
    public void testSubmitAssessmentModifyRemovePoints() throws NetworkException {
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
        
        // Set intial points
        int initialPoints = 10;
        assessment.setAchievedPoints(initialPoints);
        
        // Upload assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Test that assessment does now exist on server
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
        Assertions.assertEquals(initialPoints, this.assessment.getAchievedPoints());
        
        // Modify assessment: Delete points as correction wasn't correct
        this.assessment.setAchievedPoints(0);
        
        // Upload assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, this.assessment));
        
        // Test that points were also removed on server
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
        Assertions.assertEquals(0, this.assessment.getAchievedPoints(), "Achieved points were not deleted on server.");
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
    @Order(40)
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
        Assertions.assertEquals(tool, partial.getKey());
        Assertions.assertEquals(tool, partial.getTitle());
        Assertions.assertEquals(severity, partial.getMarkers().get(0).getSeverity().name());
        Assertions.assertEquals(file, partial.getMarkers().get(0).getPath());
        Assertions.assertEquals(line.intValue(), partial.getMarkers().get(0).getStartLineNumber().intValue());
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#submitAssessment(Assignment, Assessment)} can submit
     * an {@link Assessment} with incomplete markers: Only a severity
     */
    @Order(41)
    @Test
    public void testIncompleteMarkerOfPartials_OnlySeverity() throws NetworkException {
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
        assessment.addAutomaticReview(tool, severity, null, null, null);
        
        // Upload assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Test that assessment does now exist on server -> Read from server
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
        Assertions.assertEquals(1, this.assessment.partialAsssesmentSize());
        
        // Test partial assessment
        PartialAssessmentDto partial = this.assessment.getPartialAssessment(0);
        Assertions.assertEquals(tool, partial.getKey());
        Assertions.assertEquals(tool, partial.getTitle());
        Assertions.assertEquals(severity, partial.getMarkers().get(0).getSeverity().name());
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#submitAssessment(Assignment, Assessment)} can submit
     * an {@link Assessment} with incomplete markers: Only a message
     */
    @Order(41)
    @Test
    public void testIncompleteMarkerOfPartials_OnlyMessage() throws NetworkException {
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
        String description = "Classes do not compile";
        assessment.addAutomaticReview(tool, null, description, null, null);
        
        // Upload assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Test that assessment does now exist on server -> Read from server
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
        Assertions.assertEquals(1, this.assessment.partialAsssesmentSize());
        
        // Test partial assessment
        PartialAssessmentDto partial = this.assessment.getPartialAssessment(0);
        Assertions.assertEquals(tool, partial.getKey());
        Assertions.assertEquals(tool, partial.getTitle());
        Assertions.assertEquals(description, partial.getComment());
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
    @Order(42)
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
        String tool = "javac";
        String severity = SeverityEnum.ERROR.name();
        String description = "Classes do not compile";
        assessment.addAutomaticReview(tool, severity, description, "File.java", 42);
        
        // Upload assessment: Basis for the test!
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Read assessment from server and modify the partial
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
        Assertions.assertEquals(1, this.assessment.partialAsssesmentSize());
        Assertions.assertNotNull(this.assessment.getPartialAssessment(0));
        this.assessment.clearPartialAssessments("javac");
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
        Assertions.assertNotNull(partial);
        Assertions.assertEquals(tool, partial.getTitle());
        Assertions.assertEquals(severity, partial.getMarkers().get(0).getSeverity().name());
        Assertions.assertEquals(file, partial.getMarkers().get(0).getPath());
        Assertions.assertEquals(line.intValue(), partial.getMarkers().get(0).getStartLineNumber().intValue());
    }
    
    /**
     * Tests that {@link SubmissionHookProtocol#submitAssessment(Assignment, Assessment)} can submit
     * an {@link Assessment}. Parameters:
     * <ul>
     *   <li>Group assignment</li>
     *   <li>Modify assessment</li>
     *   <li>Delete all partials (students fixed all problems)</li>
     * </ul>
     */
    @Order(42)
    @Test
    public void testSubmitAssessmentDeletePartials() throws NetworkException {
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
        String tool2 = "Checkstyle";
        String severity2 = SeverityEnum.ERROR.name();
        String description2 = "Classes are not well structured";
        assessment.addAutomaticReview(tool2, severity2, description2, "File.java", 21);
        
        // Upload assessment: Basis for the test!
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
        
        // Read assessment from server and clear the partials
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
        Assertions.assertEquals(2, this.assessment.partialAsssesmentSize());
        Assertions.assertNotNull(this.assessment.getPartialAssessment(0));
        Assertions.assertNotNull(this.assessment.getPartialAssessment(1));
        this.assessment.clearPartialAssessments("Compiler", "Checkstyle");
        
        // Upload modified assessment
        Assertions.assertTrue(hook.submitAssessment(assignment, this.assessment));
        
        // Test that partials were deleted
        this.assessment = hook.loadAssessmentByName(assignment, group);
        assertAssessment(this.assessment, true);
        Assertions.assertEquals(0, this.assessment.partialAsssesmentSize());
    }
    
    /**
     * Tests that <b>Partial</b>Assessments can be created during the submission on the fly.
     */
    @Order(50)
    @Test
    public void testCreatePartialDuringSubmission() throws NetworkException {
        String expectedAssignment = "Test_Assignment 06 (Java) Testat In Progress";
        String user = "elshar";
        
        // Load Assignment data
        SubmissionHookProtocol hook = initProtocol();
        Assignment assignment = hook.getAssignmentByName(expectedAssignment);
        assertAssignment(assignment, State.SUBMISSION, TestUtils.TEST_DEFAULT_SUBMITABLE_ASSIGNMENT_SINGLE);
        // Marks this.assessment for removal via the cleanUp-Method
        protocol = hook.getProtocol();
        
        // Create Assessment data
        this.assessment = hook.loadAssessmentByName(assignment, user);
        String tool = "Compiler";
        String severity = SeverityEnum.ERROR.name();
        String description = "Classes do not compile";
        assessment.addAutomaticReview(tool, severity, description, "File.java", 42);
        
        // Upload assessment (during submission to upload partial assessment)
        Assertions.assertTrue(hook.submitAssessment(assignment, assessment));
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
