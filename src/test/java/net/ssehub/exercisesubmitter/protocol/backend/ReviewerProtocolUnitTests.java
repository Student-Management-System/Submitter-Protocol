package net.ssehub.exercisesubmitter.protocol.backend;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssessmentsApi;
import net.ssehub.studentmgmt.backend_api.api.CoursesApi;
import net.ssehub.studentmgmt.backend_api.model.AssessmentCreateDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.CourseDto;
import net.ssehub.studentmgmt.backend_api.model.GroupDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.UserDto;

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
    private static final File TEST_FOLDER = new File("src/test/resources");

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
                assessmentApiMock, null);
        
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
                assessmentApiMock, null);
        
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
                assessmentApiMock, null);
        
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
                assessmentApiMock, null);
        
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
     * Tests if the returned String with the user and assessment informations is correct formated.
     */
    @Disabled
    @Test
    public void testGetSubmissionRealUsersReviews() {
        
        // Mock: Simulate return of an assessment on server
        AssessmentsApi assessmentApiMock = Mockito.mock(AssessmentsApi.class);
        try {
            Mockito.when(assessmentApiMock.getAllAssessmentsForAssignment(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(createAssessmentList());
        } catch (ApiException e) {
            Assertions.fail("Should not be thrown " + e.getMessage());
        }
       
        ReviewerProtocol rp = new ReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME, null, createCourseMock(), null,
                assessmentApiMock, null);
        
        String actual = "";
        try {
            actual = rp.getSubmissionRealUsersReviews(TEST_ASSIGNMENT_ID);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        
        String expected = readReviewFile("submissionRealUsersReviews");
        Assertions.assertEquals(expected, actual);
    }
    
    /**
     * Tests if the returned String with the group and user informations is correct formated.
     */
    @Disabled
    @Test
    public void testGetSubmissionReviewerUsers() {
        
        // Mock: Simulate return of an assessment on server
        AssessmentsApi assessmentApiMock = Mockito.mock(AssessmentsApi.class);
        try {
            Mockito.when(assessmentApiMock.getAllAssessmentsForAssignment(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(createAssessmentList());
        } catch (ApiException e) {
            Assertions.fail("Should not be thrown " + e.getMessage());
        }
       
        ReviewerProtocol rp = new ReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME, null, createCourseMock(), null,
                assessmentApiMock, null);
        
        String actual = "";
        try {
            actual = rp.getSubmissionReviewerUsers(TEST_ASSIGNMENT_ID);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        
        String expected = readReviewFile("submissionReviewer");
        Assertions.assertEquals(expected, actual);
    }
    
    /**
     *  Tests if the returned String with the group and review informations is correct formated.
     */
    @Disabled
    @Test
    public void testGetSubmissionReviews() {
        
        // Mock: Simulate return of an assessment on server
        AssessmentsApi assessmentApiMock = Mockito.mock(AssessmentsApi.class);
        try {
            Mockito.when(assessmentApiMock.getAllAssessmentsForAssignment(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(createAssessmentList());
        } catch (ApiException e) {
            Assertions.fail("Should not be thrown " + e.getMessage());
        }
       
        ReviewerProtocol rp = new ReviewerProtocol(TEST_BASE_PATH, TEST_COURSE_NAME, null, createCourseMock(), null,
                assessmentApiMock, null);
        
        String actual = "";
        try {
            actual = rp.getSubmissionReviews(TEST_ASSIGNMENT_ID);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        
        String expected = readReviewFile("submissionReviews");
        Assertions.assertEquals(expected, actual);
    }
    
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
     * Creates a list of assessments for the tests.
     * @return List of assessments.
     */
    private static List<AssessmentDto> createAssessmentList() {
        // creates the assignment
        AssignmentDto assignment = new AssignmentDto();
        assignment.setPoints(new BigDecimal(11));
        assignment.setName("Test_Aufgabe");
        
        UserDto user1 = new UserDto();
        user1.setUsername("Max Mustermann");
        user1.setEmail("mustermann@test.de");
        user1.setRzName("mustermann");
        
        UserDto user2 = new UserDto();
        user2.setUsername("John Doe");
        user2.setEmail("doe@test.de");
        user2.setRzName("doe");
        
        UserDto user3 = new UserDto();
        user3.setUsername("Peter Pan");
        user3.setEmail("pan@test.de");
        user3.setRzName("pan");
        
        // creates the groups
        GroupDto group1 = new GroupDto();
        group1.setName("JP001");
        group1.setUsers(Arrays.asList(user1, user2));
        
        GroupDto group2 = new GroupDto();
        group2.setName("JP002");
        group2.setUsers(Arrays.asList(user3));
        
        
        // creates the assessments
        AssessmentDto assessment1 = new AssessmentDto();
        assessment1.setAssignmentId(TEST_ASSIGNMENT_ID);
        assessment1.setId("001");
        assessment1.setAssignment(assignment);
        assessment1.setGroup(group1);
        assessment1.setAchievedPoints(new BigDecimal(10));
        assessment1.setComment("Du hast was vergessen");
        
        AssessmentDto assessment2 = new AssessmentDto();
        assessment2.setAssignmentId(TEST_ASSIGNMENT_ID);
        assessment2.setId("002");
        assessment2.setAssignmentId(TEST_ASSIGNMENT_ID);
        assessment2.setComment("Perfekt");
        assessment2.setGroup(group2);
        assessment2.setAchievedPoints(new BigDecimal(11));
        assessment2.setAssignment(assignment);
        
        List<AssessmentDto> assessments = new ArrayList<>();
        assessments.add(assessment1);
        assessments.add(assessment2);
        
        return assessments;
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
