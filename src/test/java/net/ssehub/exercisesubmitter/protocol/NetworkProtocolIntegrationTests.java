package net.ssehub.exercisesubmitter.protocol;


import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.swagger.client.model.AssessmentDto;
import io.swagger.client.model.AssignmentDto;
import io.swagger.client.model.AssignmentDto.StateEnum;
import io.swagger.client.model.CourseDto;
import io.swagger.client.model.GroupDto;

/**
 * This class declares <b>integration</b> tests for the {@link NetworkProtocol} class.
 * These tests communicates with the REST test server.
 * 
 * @author Kunold
 *
 */
public class NetworkProtocolIntegrationTests {
    /**
     * The test data.
     */
    public static final String TEST_SERVER = "http://147.172.178.30:3000";
    public static final String TEST_COURSE_ID = "java";
    public static final String TEST_USER_ID = "a019ea22-5194-4b83-8d31-0de0dc9bca53";
    public static final String TEST_SEMESTER = "wise1920";

    /**
     * Test if the REST server is not found.
     */
    @Test
    public void testServerNotFound() {
        NetworkProtocol np = new NetworkProtocol("NON_EXISTING_SERVER", "a_course");
        try {
            np.getCourses("userID");
            Assertions.fail("Expected ServerNotFoundException, but did not occur.");
        } catch (ServerNotFoundException e) {
            Assertions.assertEquals("NON_EXISTING_SERVER", e.getURL());
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Test if a course to a id is found.
     */
    @Test
    public void testGetCourseID() {
        NetworkProtocol np = new NetworkProtocol(TEST_SERVER, TEST_COURSE_ID);
        np.setSemester(TEST_SEMESTER);
        try {
            String course = np.getCourseID();
            Assertions.assertFalse(course.isEmpty(), "No course found");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Test if a List of courses is returned.
     */
    @Test
    public void testListOfCourses() {
        NetworkProtocol np = new NetworkProtocol(TEST_SERVER, TEST_COURSE_ID);
        np.setSemester(TEST_SEMESTER);
        try {
            List<CourseDto> courses = np.getCourses(TEST_USER_ID);
            Assertions.assertNotNull(courses, "Course list was null, but should never be null.");
            Assertions.assertFalse(courses.isEmpty(), "List of courses was empty");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Test if a List of groups is returned.
     */
    @Test
    public void testGetGroup() {
        NetworkProtocol np = new NetworkProtocol(TEST_SERVER, TEST_COURSE_ID);
        np.setSemester(TEST_SEMESTER);
        try {
            List<GroupDto> groups = np.getGroups(TEST_USER_ID);
            Assertions.assertNotNull(groups, "Group list was null, but should never be null.");
            Assertions.assertFalse(groups.isEmpty(), "List of groups was empty");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Test if a List of assignments is returned.
     */
    @Test
    public void testGetAssignments() {
        NetworkProtocol np = new NetworkProtocol(TEST_SERVER, TEST_COURSE_ID);
        np.setSemester(TEST_SEMESTER);
        try {
            List<AssignmentDto> assignments = np.getAssignments();
            Assertions.assertNotNull(assignments, "Assignment list was null, but should never be null.");
            Assertions.assertFalse(assignments.isEmpty(), "List of assignments was empty");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }  
    }
    
    /**
     * Test if a List of assessments is returned.
     */
    //TODO TK: deactivated till API query is fixed
    @Disabled
    @Test
    public void testGetAssessmentsWithGoups() {
        NetworkProtocol np = new NetworkProtocol(TEST_SERVER, TEST_COURSE_ID);
        np.setSemester(TEST_SEMESTER);
        try {
            List<AssessmentDto> assessments = np.getAssessmentsWithGroups(TEST_USER_ID);
            Assertions.assertNotNull(assessments, "Assessment list was null, but should never be null.");
            Assertions.assertFalse(assessments.isEmpty(), "List of assessments was empty");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Test if a Map of assignments of all specified submissions and their permissions is returned.
     */
    @Test
    public void testReadPermissions() {
        NetworkProtocol np = new NetworkProtocol(TEST_SERVER, TEST_COURSE_ID);
        np.setSemester(TEST_SEMESTER);
        Map <String, StateEnum> assignments = np.readPermissions();
        Assertions.assertNotNull(assignments, "Assignment map was null, but should never be null.");
        Assertions.assertFalse(assignments.isEmpty(), "Map of assignments was empty");
    }

}
