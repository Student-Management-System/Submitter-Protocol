package net.ssehub.exercisesubmitter.protocol.frontend;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssessmentsApi;
//import net.ssehub.studentmgmt.backend_api.api.CoursesApi;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
//import net.ssehub.studentmgmt.backend_api.model.CourseDto;


/**
 * Tests the {@link ExerciseReviewerProtocol} <b>without</b> querying the REST server.
 * @author Kunold
 *
 */
public class ExerciseReviewerProtocolUnitTests {

    public static final File TEST_FOLDER = new File("src/test/resources");
    private static final String TEST_BASE_PATH = "http://147.172.178.30:3000";
    private static final String TEST_COURSE_NAME = "java";
    private static final String TEST_ASSIGNMENT_ID = "001";
    
    /**
     * Reads the review file with the data to test.
     * @param fileName the name of the file that contains the test data.
     * @return a String with the test data that is read from the file.
     */
    private static String readReviewFile(String fileName) {
        String content = null;
        // Based on https://www.geeksforgeeks.org/different-ways-reading-text-file-java/
        File path = new File(TEST_FOLDER, fileName);
        try {
            content = Files.readString(path.toPath()).trim();
            // next line need to be commented out under windows since lineSeperator has another behavior on windows, 
            // than under linux
            //content = content.replace("\n", System.lineSeparator());
        } catch (IOException e) {
            Assertions.fail("Could not read configuration from " + path.getAbsolutePath(), e);
        }
        
        return content;
    }
    
    /**
     *  Tests if the returned String with the group and review informations is correct formated.
     */
    @Disabled
    @Test
    public void testGetSubmissionReviews() {
        
        AssessmentDto assessment1 = new AssessmentDto();
        assessment1.setAssignmentId(TEST_ASSIGNMENT_ID);
        assessment1.setId("001");
        AssessmentDto assessment2 = new AssessmentDto();
        assessment2.setAssignmentId(TEST_ASSIGNMENT_ID);
        assessment2.setId("002");
        
        List<AssessmentDto> assessments = new ArrayList<>();
        assessments.add(assessment1);
        assessments.add(assessment2);
        
        // Mock: Simulate return of an assessment on server
        AssessmentsApi assessmentApiMock = Mockito.mock(AssessmentsApi.class);
        try {
            Mockito.when(assessmentApiMock.getAllAssessmentsForAssignment(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(assessments);
        } catch (ApiException e) {
            Assertions.fail("Should not be thrown " + e.getMessage());
        }
        
        ExerciseReviewerProtocol erp = new ExerciseReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME);
        
        String actual = erp.getSubmissionReviews(TEST_ASSIGNMENT_ID);
        
        String expected = readReviewFile("submissionReviews");
        System.out.println(expected);
        Assertions.assertEquals(expected, actual);
    }
    
}
