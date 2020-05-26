package net.ssehub.exercisesubmitter.protocol.backend;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssessmentsApi;
import net.ssehub.studentmgmt.backend_api.api.CoursesApi;
import net.ssehub.studentmgmt.backend_api.model.AssessmentCreateDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.CourseDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;

/**
 * This class declares <b>unit</b> tests for the {@link ReviewerProtocol} class.
 * These tests won't communicate with the REST test server.
 * @author Kunold
 *
 */
public class ReviewerProtocolUnitTests {
    
    private static final String TEST_COURSE_NAME = "java";
    private static final String TEST_BASE_PATH = "http://147.172.178.30:3000";
    private static final String TEST_ASSIGNMENT_ID = "001";
    private static final String TEST_ASSESSMENT_ID = "002";
    private static final String TEST_COURSE_ID = "java-wise1920";

    /**
     * Tests if an Assessment is created.
     */
    @Test
    public void testCreateAssessment() {
        
        AssessmentCreateDto body = new AssessmentCreateDto();
        body.setAchievedPoints(new BigDecimal(10));
        
        // Mock: Simulate creation of an assessment on server
        AssessmentsApi assessmentApiMock = Mockito.mock(AssessmentsApi.class);
        try {
            Mockito.when(assessmentApiMock.createAssessment(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new AssessmentDto());
        } catch (ApiException e) {
            Assertions.fail("Should not be thrown " + e.getMessage());
        }
        
        ReviewerProtocol rp = new ReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME, null, createCourseMock(), null,
                assessmentApiMock);
        
        try {
            boolean check = rp.createAssessment(body, TEST_ASSIGNMENT_ID);
            Assertions.assertTrue(check);
            // Check if server mock was used
            Mockito.verify(assessmentApiMock).createAssessment(body, TEST_COURSE_ID, TEST_ASSIGNMENT_ID);
        } catch (NetworkException | ApiException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
        
    }

    /**
     * Tests if an PartialAssessment is created.
     */
    @Test
    public void testCreatePartialAssessment() {
        
        PartialAssessmentDto body = new PartialAssessmentDto();
        body.setPoints(new BigDecimal(10));
        
        // Mock: Simulate creation of an partial assessment on server
        AssessmentsApi assessmentApiMock = Mockito.mock(AssessmentsApi.class);
        try {
            Mockito.when(assessmentApiMock.addPartialAssessment(Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                    Mockito.anyString()))
                .thenReturn(new PartialAssessmentDto());
        } catch (ApiException e) {
            Assertions.fail("Should not be thrown " + e.getMessage());
        }
        
        ReviewerProtocol rp = new ReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME, null, createCourseMock(), null,
                assessmentApiMock);
        
        try {
            boolean check = rp.createPartialAssessment(body, TEST_ASSIGNMENT_ID, TEST_ASSESSMENT_ID);
            Assertions.assertTrue(check);
            // Check if server mock was used
            Mockito.verify(assessmentApiMock).addPartialAssessment(body, TEST_COURSE_ID, TEST_ASSIGNMENT_ID,
                    TEST_ASSESSMENT_ID);
        } catch (NetworkException | ApiException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Tests if an Assessment is updated.
     */
    @Test
    public void testUpdateAssessment() {
        AssessmentDto body = new AssessmentDto();
        body.setAchievedPoints(new BigDecimal(10));
        
        // Mock: Simulate update of an assessment on server
        AssessmentsApi assessmentApiMock = Mockito.mock(AssessmentsApi.class);
        try {
            Mockito.when(assessmentApiMock.updateAssessment(Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                    Mockito.anyString()))
                .thenReturn(new AssessmentDto());
        } catch (ApiException e) {
            Assertions.fail("Should not be thrown " + e.getMessage());
        }
        
        ReviewerProtocol rp = new ReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME, null, createCourseMock(), null,
                assessmentApiMock);
        
        try {
            boolean check = rp.updateAssessment(body, TEST_ASSIGNMENT_ID, TEST_ASSESSMENT_ID);
            Assertions.assertTrue(check);
            // Check if server mock was used
            Mockito.verify(assessmentApiMock).updateAssessment(body, TEST_COURSE_ID, TEST_ASSIGNMENT_ID, 
                    TEST_ASSESSMENT_ID);
        } catch (NetworkException | ApiException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Tests if an Assessment is deleted.
     */
    @Test
    public void testDeleteAssessment() {

        boolean result = true;
        
        // Mock: Simulate deletion of an assessment on server
        AssessmentsApi assessmentApiMock = Mockito.mock(AssessmentsApi.class);
        try {
            Mockito.when(assessmentApiMock.deleteAssessment(Mockito.anyString(), Mockito.anyString(), 
                    Mockito.anyString()))
                .thenReturn(result);
        } catch (ApiException e) {
            Assertions.fail("Should not be thrown " + e.getMessage());
        }
        
        ReviewerProtocol rp = new ReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME, null, createCourseMock(), null,
                assessmentApiMock);
        
        try {
            boolean check = rp.deleteAssessment(TEST_ASSIGNMENT_ID, TEST_ASSESSMENT_ID);
            Assertions.assertTrue(check);
            // Check if server mock was used
            Mockito.verify(assessmentApiMock).deleteAssessment(TEST_COURSE_ID, TEST_ASSIGNMENT_ID, 
                    TEST_ASSESSMENT_ID);
        } catch (NetworkException | ApiException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Creates an mock for the CoursesApi.
     * @return the mocked CoursesApi.
     */
    private CoursesApi createCourseMock() {
        CoursesApi courseApiMock = Mockito.mock(CoursesApi.class);
        try {
            CourseDto result = new CourseDto();
            result.setId(TEST_COURSE_ID);
            Mockito.when(courseApiMock.getCourseByNameAndSemester(Mockito.anyString(), Mockito.any()))
                .thenReturn(result);
        } catch (ApiException e) {
            Assertions.fail("Should not be thrown " + e.getMessage());
        }
        return courseApiMock;
    }

}
