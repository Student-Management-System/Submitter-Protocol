package net.ssehub.exercisesubmitter.protocol.backend;


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
     * Test if a List of courses is returned.
     */
    @Test
    public void testListOfCourses() {
        NetworkProtocol np = initProtocol(false);
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
     * Test if a List of assessments is returned.
     */
    @Test
    public void testGetAssessmentsWithGoups() {
        NetworkProtocol np = initProtocol(false);
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
