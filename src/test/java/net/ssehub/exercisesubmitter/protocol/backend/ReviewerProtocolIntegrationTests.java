package net.ssehub.exercisesubmitter.protocol.backend;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;


/**
 * This class declares integration tests for the {@link ReviewerProtocol} class.
 * Requires valid credentials for testing provided via VM arguments. If no credentials are provided,
 * test will be skipped (and marked yellow in Jenkins).
 * Required properties are:
 * <ul>
 *   <li>test_user</li>
 *   <li>test_password</li>
 * </ul>
 * 
 * @author Kunold
 * @author El-Sharkawy
 *
 */
public class ReviewerProtocolIntegrationTests { 
    /**
     * The test data.
     */
    public static final String TEST_SERVER = "http://147.172.178.30:3000";
    public static final String TEST_COURSE_ID = "java";
    public static final String TEST_ASSIGNMENT_ID = "993b3cd0-6207-11ea-bc55-0242ac130003";
    public static final String TEST_ASSESSMENT_ID = "680dd44a-93b0-4d1c-a947-9b50a4bbb68e";
    public static final String TEST_SEMESTER = "wise1920";
    
    
    /**
     * Test that {@link DataNotFoundException} is thrown if no assessments are found.
     */
    @Test
    public void testGetAssessmentsDataNotFound() { 
        // Requires a valid user to be logged in
        LoginComponent login = LoginComponentIntegrationTests.createLoginForTests();
        
        ReviewerProtocol rp = new ReviewerProtocol(TEST_SERVER, TEST_COURSE_ID);
        rp.setSemester(TEST_SEMESTER);
        rp.setAccessToken(login.getManagementToken());
        
        // Test that DataNotFoundException is correctly thrown
        Exception exception = assertThrows(DataNotFoundException.class, 
            () -> rp.getAssessments("no_ID", null));
        Assertions.assertEquals("Assessments not found", exception.getMessage());
    }
    
    /**
     * Tests if a List of Assessments for an Assignment is returned.
     */
    @Test
    public void testGetAssessments() {
        // Requires a valid user to be logged in
        LoginComponent login = LoginComponentIntegrationTests.createLoginForTests();
        
        ReviewerProtocol rp = new ReviewerProtocol(TEST_SERVER, TEST_COURSE_ID);
        rp.setSemester(TEST_SEMESTER);
        rp.setAccessToken(login.getManagementToken());
        
        try {
            List <AssessmentDto> assessments = rp.getAssessments(TEST_ASSIGNMENT_ID, null);
            Assertions.assertNotNull(assessments, "Assessment List was null, but never should be null");
            Assertions.assertFalse(assessments.isEmpty(), "List of Assessments was empty");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Test that {@link DataNotFoundException} is thrown if no assessments for a assignment are found.
     */
    @Test
    public void testGetAssessmentForAssignmentDataNotFound() {
        // Requires a valid user to be logged in
        LoginComponent login = LoginComponentIntegrationTests.createLoginForTests();
        
        ReviewerProtocol rp = new ReviewerProtocol(TEST_SERVER, TEST_COURSE_ID);
        rp.setSemester(TEST_SEMESTER);
        rp.setAccessToken(login.getManagementToken());

        // Test that DataNotFoundException is correctly thrown
        Exception exception = assertThrows(DataNotFoundException.class, 
            () -> rp.getAssessmentForAssignment("no_ID", "no_ID"));
        Assertions.assertEquals("Assessments for the specified assignment not found", exception.getMessage());
    }
    
    /**
     * Tests if a single Assessment for an Assignment is returned.
     */
    @Test
    public void testGetAssessmentForAssignment() {
        // Requires a valid user to be logged in
        LoginComponent login = LoginComponentIntegrationTests.createLoginForTests();
        
        ReviewerProtocol rp = new ReviewerProtocol(TEST_SERVER, TEST_COURSE_ID);
        rp.setSemester(TEST_SEMESTER);
        rp.setAccessToken(login.getManagementToken());
        
        try {
            AssessmentDto assessment = rp.getAssessmentForAssignment(TEST_ASSIGNMENT_ID, TEST_ASSESSMENT_ID);
            Assertions.assertNotNull(assessment, "No Assessment returned");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
}
