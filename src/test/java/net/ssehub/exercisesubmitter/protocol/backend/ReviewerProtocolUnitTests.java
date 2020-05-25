package net.ssehub.exercisesubmitter.protocol.backend;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssessmentsApi;
import net.ssehub.studentmgmt.backend_api.model.AssessmentCreateDto;

/**
 * This class declares <b>unit</b> tests for the {@link ReviewerProtocol} class.
 * These tests won't communicate with the REST test server.
 * @author Kunold
 *
 */
public class ReviewerProtocolUnitTests {
    
    private static final String TEST_COURSE_NAME = "java";
    //private static final String TEST_BASE_PATH = "Path";
    private static final String TEST_ASSIGNMENT_ID = "001";

    /**
     * Tests if a Assessment is created.
     */
    @Test
    public void testCreateAssessment() {
        
        AssessmentCreateDto body = new AssessmentCreateDto();
        body.setAchievedPoints(new BigDecimal(10));
        
        AssessmentsApi assessmentApiMock = Mockito.mock(AssessmentsApi.class);
        
        //ReviewerProtocol rp = new ReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME, assessmentApiMock);
        //ReviewerProtocol rp = Mockito.mock(ReviewerProtocol.class);
        //rp = new ReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME, assessmentApiMock);
        
        try {
            assessmentApiMock.createAssessment(body, TEST_COURSE_NAME, TEST_ASSIGNMENT_ID);
            //rp.createAssessment(body, TEST_ASSIGNMENT_ID);
        } catch (ApiException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
        
    }

}
