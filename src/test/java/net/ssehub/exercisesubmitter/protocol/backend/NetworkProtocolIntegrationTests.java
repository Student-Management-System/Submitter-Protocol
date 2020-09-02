package net.ssehub.exercisesubmitter.protocol.backend;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
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
 * @author El-Sharkawy
 *
 */
public class NetworkProtocolIntegrationTests {
    /**
     * The test data.
     */
    public static final String TEST_USER_ID = "a019ea22-5194-4b83-8d31-0de0dc9bca53";
    public static final String TEST_ASSIGNMENT_ID = "75b799a1-a406-419b-a448-909aa3d34afa";
    
    private static String accessToken;

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
     * Test that {@link ServerNotFoundException} is thrown if server is not found.
     */
    @Test
    public void testGetCourseIDServerNotFound() {
        // Init protocol
        NetworkProtocol np = new NetworkProtocol("not_existing_server",
            "not_existing_course");
        // set not existing semester
        np.setSemester("not_existing_semester");
        
        // Test that ServerNotFoundException is correctly thrown
        Exception exception = assertThrows(ServerNotFoundException.class, 
            () -> np.getCourseID());
        Assertions.assertEquals("unexpected url: not_existing_server/courses?shortname=not_existing_course&" 
            + "semester=not_existing_semester", exception.getMessage());
    }
    
    /**
     * Test that {@link DataNotFoundException} is thrown if courseID is not found.
     */
    @Test
    public void testGetCourseIDDataNotFound() {
        // Init protocol
        NetworkProtocol np = new NetworkProtocol(TestUtils.TEST_MANAGEMENT_SERVER,
            TestUtils.TEST_DEFAULT_JAVA_COURSE);
        // set not existing semester
        np.setSemester("not_existing_semester");
        
        // Test that DataNotFoundException is correctly thrown
        Exception exception = assertThrows(DataNotFoundException.class, 
            () -> np.getCourseID());
        Assertions.assertEquals("Course not found", exception.getMessage());
    }
    
    /**
     * Test if a course to a id is found.
     */
    @Test
    public void testGetCourseID() {
        NetworkProtocol np = initProtocol(false);
        try {
            String course = np.getCourseID();
            Assertions.assertFalse(course.isEmpty(), "No course found");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Test that {@link DataNotFoundException} is thrown if no list of courses is found.
     */
    @Test
    public void testListOfCoursesDataNotFound() {
        NetworkProtocol np = initProtocol(true);
        
        // Test that DataNotFoundException is correctly thrown
        Exception exception = assertThrows(DataNotFoundException.class, 
            () -> np.getCourses("no_ID"));
        Assertions.assertEquals("User not found", exception.getMessage());
    }
    
    /**
     * Test if a List of courses is returned.
     */
    @Test
    public void testListOfCourses() {
        NetworkProtocol np = initProtocol(true);
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
        NetworkProtocol np = initProtocol(true);      
        try {
            List<Assignment> assignments = np.getAssignments((StateEnum[]) null);
            Assertions.assertNotNull(assignments, "Assignment list was null, but should never be null.");
            Assertions.assertFalse(assignments.isEmpty(), "List of assignments was empty");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }  
    }
    
    /**
     * Test that {@link DataNotFoundException} is thrown if no assessment with groups is found.
     */
    @Test
    public void testGetAssessmentsWithGroupsDataNotFound() {
        NetworkProtocol np = initProtocol(true);
        
        // Test that DataNotFoundException is correctly thrown
        Exception exception = assertThrows(DataNotFoundException.class, 
            () -> np.getAssessmentsWithGroups("no_ID"));
        Assertions.assertEquals("Assessments not found", exception.getMessage());
    }
    
    /**
     * Test if a List of assessments is returned.
     */
    @Test
    public void testGetAssessmentsWithGoups() {
        NetworkProtocol np = initProtocol(true);        
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
        NetworkProtocol np = initProtocol(true);
        Map <String, State> assignments = np.readPermissions();
        Assertions.assertNotNull(assignments, "Assignment map was null, but should never be null.");
        Assertions.assertFalse(assignments.isEmpty(), "Map of assignments was empty");
    }
    
    /**
     * Test that {@link DataNotFoundException} is thrown if no groups at submission end are found.
     */
    @Test
    public void testGetGroupsAtSubmissionEndDataNotFound() {
        NetworkProtocol np = initProtocol(true);
        
        // Test that DataNotFoundException is correctly thrown
        Exception exception = assertThrows(DataNotFoundException.class, 
            () -> np.getGroupsAtAssignmentEnd("no_ID"));
        Assertions.assertEquals("No Groups for the assignment found", exception.getMessage());
    }
    
    /**
     * Tests if a list of groups at submission end is returned.
     */
    @Test
    public void testGetGroupsAtSubmissionEnd() {
        NetworkProtocol np = initProtocol(true);    
        try {
            List<GroupDto> groups = np.getGroupsAtAssignmentEnd(TEST_ASSIGNMENT_ID);
            Assertions.assertNotNull(groups, "Groups list was null, but should never be null.");
            Assertions.assertFalse(groups.isEmpty(), "List of groups was empty");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Test that {@link DataNotFoundException} is thrown if no group for the assignment is found.
     */
    @Test
    public void testGetGroupForAssignmentDataNotFound() {
        NetworkProtocol np = initProtocol(true);
        
        // Test that DataNotFoundException is correctly thrown
        Exception exception = assertThrows(DataNotFoundException.class, 
            () -> np.getGroupForAssignment("no_ID", "no_ID"));
        Assertions.assertEquals("No assignment related group information found", exception.getMessage());
    }
    
    /**
     * Tests if a groupname for an assignment is returned.
     */
    @Test
    public void testGetGroupForAssignment() {
        NetworkProtocol np = initProtocol(true);
        try {
            String groupName = np.getGroupForAssignment(TEST_USER_ID, TEST_ASSIGNMENT_ID);
            Assertions.assertFalse(groupName.isEmpty(), "Groupname was empty");
        } catch (NetworkException e) {
            Assertions.fail("Unexpected NetworkException returned: " + e.getMessage());
        }
    }
    
    /**
     * Tests Getter for the base path, course name, and groupsAPI.
     */
    @Test
    public void testGetter( ) {
        NetworkProtocol np = initProtocol(false);
        Assertions.assertNotNull(np.getBasePath());
        Assertions.assertEquals(TestUtils.TEST_MANAGEMENT_SERVER, np.getBasePath());
        Assertions.assertNotNull(np.getCourseName());
        Assertions.assertEquals(TestUtils.TEST_DEFAULT_JAVA_COURSE, np.getCourseName());
        Assertions.assertNotNull(np.getGroupsApi());
    }
    
    /**
     * Creates an {@link NetworkProtocol} with default settings and logs in a tutor.
     * Useful for tests of APIs that require an authorized user.
     * @param requiresLogin <tt>true</tt> tests an authorized API and requires a valid user. In this case,
     *     {@link TestUtils#retreiveAccessToken()} is used to login a user via VM arguments.
     * @return {@link NetworkProtocol} usable for testing.
     */
    private NetworkProtocol initProtocol(boolean requiresLogin) {
        // Init protocol
        NetworkProtocol protocol = new NetworkProtocol(TestUtils.TEST_MANAGEMENT_SERVER,
            TestUtils.TEST_DEFAULT_JAVA_COURSE);
        protocol.setSemester(TestUtils.TEST_DEFAULT_SEMESTER);
        
        if (requiresLogin) {
            if (null == accessToken) {
                // Logs the user in only at the first test and re-uses the token
                accessToken = TestUtils.retreiveAccessToken();
            }
            protocol.setAccessToken(accessToken);
        }
        return protocol;
    }
}
