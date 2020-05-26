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

    /**
     * Tests if a Assessment is created.
     */
    @Test
    public void testCreateAssessment() {
        
        AssessmentCreateDto body = new AssessmentCreateDto();
        body.setAchievedPoints(new BigDecimal(10));
        
        AssessmentsApi assessmentApiMock = Mockito.mock(AssessmentsApi.class);
        try {
            Mockito.when(assessmentApiMock.createAssessment(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new AssessmentDto());
        } catch (ApiException e1) {
            Assertions.fail("Should not be thrown " + e1.getMessage());
        }
        
        CoursesApi courseApiMock = Mockito.mock(CoursesApi.class);
        
        try {
            CourseDto result = new CourseDto();
            result.setId("java-wise1920");
            Mockito.when(courseApiMock.getCourseByNameAndSemester(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(result);
        } catch (ApiException e) {
            Assertions.fail("Should not be thrown " + e.getMessage());
        }
        
        ReviewerProtocol rp = new ReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME, null, courseApiMock, null,
                assessmentApiMock);
        
        try {
            boolean check = rp.createAssessment(body, TEST_ASSIGNMENT_ID);
            Assertions.assertTrue(check);
            Mockito.verify(assessmentApiMock).createAssessment(body, TEST_COURSE_NAME, TEST_ASSIGNMENT_ID);
        } catch (NetworkException | ApiException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
        
    }

}
