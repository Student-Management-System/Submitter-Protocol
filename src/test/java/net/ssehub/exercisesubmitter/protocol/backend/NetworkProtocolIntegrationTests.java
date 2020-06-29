package net.ssehub.exercisesubmitter.protocol.backend;


import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.frontend.Assignment;
import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.StateEnum;
import net.ssehub.studentmgmt.backend_api.model.CourseDto;
import net.ssehub.studentmgmt.backend_api.model.GroupDto;


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
    public static final String TEST_ASSIGNMENT_ID = "75b799a1-a406-419b-a448-909aa3d34afa";
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
     * Test if a List of assignments is returned.
     */
    @Test
    public void testGetAssignments() {
        NetworkProtocol np = new NetworkProtocol(TEST_SERVER, TEST_COURSE_ID);
        np.setSemester(TEST_SEMESTER);
        try {
            List<Assignment> assignments = np.getAssignments((StateEnum[]) null);
            Assertions.assertNotNull(assignments, "Assignment list was null, but should never be null.");
            Assertions.assertFalse(assignments.isEmpty(), "List of assignments was empty");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }  
    }
    
    /**
     * Test if a List of assessments is returned.
     */
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
        Map <String, State> assignments = np.readPermissions();
        Assertions.assertNotNull(assignments, "Assignment map was null, but should never be null.");
        Assertions.assertFalse(assignments.isEmpty(), "Map of assignments was empty");
    }
    
    /**
     * Tests if a list of groups at submission end is returned.
     */
    @Test
    public void testGetGroupsAtSubmissionEnd() {
        NetworkProtocol np = new NetworkProtocol(TEST_SERVER, TEST_COURSE_ID);
        np.setSemester(TEST_SEMESTER);
        try {
            List<GroupDto> groups = np.getGroupsAtAssignmentEnd(TEST_ASSIGNMENT_ID);
            Assertions.assertNotNull(groups, "Groups list was null, but should never be null.");
            Assertions.assertFalse(groups.isEmpty(), "List of groups was empty");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
//    @Test
//    public void testGetPathOfAssessment() throws NetworkException {
//        NetworkProtocol np = new NetworkProtocol(TEST_SERVER, TEST_COURSE_ID);
//        np.setSemester(TEST_SEMESTER);
//        String groupName = np.getGroupForAssignment(TEST_USER_ID, "b2f6c008-b9f7-477f-9e8b-ff34ce339077");
//        System.out.println(groupName);
//    }

}
