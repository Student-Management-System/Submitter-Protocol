package net.ssehub.exercisesubmitter.protocol.frontend;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;
import net.ssehub.exercisesubmitter.protocol.backend.ServerNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.UnauthorizedException;
import net.ssehub.exercisesubmitter.protocol.backend.UnknownCredentialsException;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.CollaborationEnum;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.StateEnum;
import net.ssehub.studentmgmt.backend_api.model.ParticipantDto;
import net.ssehub.studentmgmt.backend_api.model.ParticipantDto.RoleEnum;

/**
 * This class declares <b>integration</b> tests for the {@link RightsManagementProtocol} class.
 * These tests communicates with the REST test server.<br/>
 * 
 * @author Kunold
 *
 */
public class RightsManagementProtocolIntegrationTests {    
    
    /**
     * Test if {@link RightsManagementProtocol#getTutors()} returns the group of tutors.
     */
    @Test
    public void testGetTutors() throws NetworkException {
        String expectedName = "Tutors_of_Course_Java";
        int expectedNumberOfTutors = 3;
        
        RightsManagementProtocol protocol = initProtocol();
        Group tutors = protocol.getTutors();
        Assertions.assertNotNull(tutors);
        Assertions.assertEquals(expectedName, tutors.getName());
        Assertions.assertEquals(expectedNumberOfTutors, tutors.getMembers().size());
    }
    
    /**
     * Test if {@link RightsManagementProtocol#getStudents()} returns the list of students.
     */
    @Test
    public void testGetStudents() throws NetworkException {
        int expectedNumberOfStudents = 4;
        
        RightsManagementProtocol protocol = initProtocol();
        List<User> students = protocol.getStudents();
        Assertions.assertFalse(students.isEmpty());
        Assertions.assertEquals(expectedNumberOfStudents, students.size());
    }
    
    /**
     * Test if {@link RightsManagementProtocol#loadGroupsPerAssignment(Assignment)} returns all groups for the specified
     * assignment.
     */
    @Test
    public void testLoadGroupsPerAssignment() throws NetworkException {
        int expectedNumberOfGroups = 3;
        
        AssignmentDto dto = new AssignmentDto();
        dto.setName("Test_Assignment 07 (Java) Testat Evaluated");
        dto.setId("75b799a1-a406-419b-a448-909aa3d34afa");
        dto.setPoints(new BigDecimal(100.0));
        dto.setState(StateEnum.EVALUATED);
        dto.setCollaboration(CollaborationEnum.SINGLE);
        Assignment assignment = new Assignment(dto);
        
        RightsManagementProtocol protocol = initProtocol();
        List<Group> groups = protocol.loadGroupsPerAssignment(assignment);
        Assertions.assertFalse(groups.isEmpty());
        Assertions.assertEquals(expectedNumberOfGroups, groups.size());
    }
    
    /**
     * Test if {@link RightsManagementProtocol#loadAssignments(List)} returns a list of assignments. 
     */
    @Test
    public void testLoadAssignments() throws NetworkException {
        int expectedNumberOfAssignments = 6;
        
        RightsManagementProtocol protocol = initProtocol();
        List<User> studentsOfCourse = protocol.getStudents();
        List<ManagedAssignment> assignments = protocol.loadAssignments(studentsOfCourse);
        Assertions.assertFalse(assignments.isEmpty());
        Assertions.assertEquals(expectedNumberOfAssignments, assignments.size());
    }
    
    /**
     * Tests that {@link RightsManagementProtocol#updateAssignment(ManagedAssignment)} sets the groups of a
     * {@link ManagedAssignment} correctly.
     * @throws NetworkException Not expected
     */
    @Test
    public void testUpdateAssignment() throws NetworkException {
        // Test data
        Set<String> expectedGroupNames = new HashSet<>(Arrays.asList("Testgroup 1", "Testgroup 2", "Testgroup 3"));
        
        // Set-up
        RightsManagementProtocol protocol = initProtocol();
        Assignment base = protocol.getOpenAssignments().stream()
            .filter(a -> a.getName().equals("Test_Assignment 01 (Java)"))
            .findAny()
            .orElse(null);
        ManagedAssignment assignment = new ManagedAssignment(base);
        
        // Test precondition: Valid assignment, but no groups
        Assertions.assertNotNull(assignment);
        Assertions.assertEquals(0, assignment.getAllGroupNames().length);
        
        // Action: Do update
        protocol.updateAssignment(assignment);
        
        // Test postcondition: Groups are stored
        Assertions.assertEquals(expectedGroupNames.size(), assignment.getAllGroupNames().length);
        String[] actualGroups = assignment.getAllGroupNames();
        for (int i = 0; i < actualGroups.length; i++) {
            Assertions.assertTrue(expectedGroupNames.contains(actualGroups[i]), "'" + actualGroups[i]
                + "' was managed as group of assignment '" + assignment.getName() + "', but not expected.");
        }
    }
    
    /**
     * Tests the automatic re-login based on {@link RightsManagementProtocol#getStudents()}.
     * @throws NetworkException Not expected
     * @throws InterruptedException Not expected
     */
    @Test
    public void testReLoginOnGetTutors() throws NetworkException, InterruptedException {
        /**
         * Mocks the ReviewerProtocol to simulate an UnauthorizedException.
         * Will throw an exception only at the very first call.
         * @author El-Sharkawy
         *
         */
        class ReviewerProtocolMock extends ReviewerProtocol {
            private boolean shouldFail = true;
            
            /**
             * Sole constructor.
             */
            public ReviewerProtocolMock() {
                super(TestUtils.TEST_MANAGEMENT_SERVER, TestUtils.TEST_DEFAULT_JAVA_COURSE);
            }
            
            @Override
            public List<ParticipantDto> getUsersOfCourse(RoleEnum... courseRoles) throws NetworkException {
                if (shouldFail) {
                    shouldFail = false;
                    throw new UnauthorizedException("Simulated time out occured.");
                }
                return super.getUsersOfCourse(courseRoles);
            }
            
        }
        
        // Configure protocol: Use mock that fails at first call
        RightsManagementProtocol protocol = new RightsManagementProtocol(TestUtils.TEST_AUTH_SERVER,
            TestUtils.TEST_MANAGEMENT_SERVER, TestUtils.TEST_DEFAULT_JAVA_COURSE, TestUtils.TEST_DEFAULT_SEMESTER);
        protocol.setNetworkComponents(null, new ReviewerProtocolMock());
        protocol.setSemester(TestUtils.TEST_DEFAULT_SEMESTER);
        String[] credentials = TestUtils.retreiveCredentialsFormVmArgs();
        try {
            protocol.login(credentials[0], credentials[1]);
        } catch (UnknownCredentialsException | ServerNotFoundException e) {
            Assumptions.assumeFalse(true, "Could not login for testing due to: " + e.getMessage());
        }
        
        // Get first token, which should change after performing the action
        String firstToken = protocol.getProtocol().getAccessToken();
        // Ensure that new token will contain new content, otherwise new token will be equal -> Timestamp: + 1 sec
        Thread.sleep(1000);
        
        // Perform the action, which requires an automatic re-login
        protocol.getStudents();
        String secondToken = protocol.getProtocol().getAccessToken();
        
        Assertions.assertNotEquals(firstToken, secondToken, "No automatic re-login was performed.");
    }
    
    /**
     * Creates an {@link RightsManagementProtocol} with default settings and logs in a tutor.
     * Useful for most tests.
     * @return {@link RightsManagementProtocol} usable for testing.
     */
    private RightsManagementProtocol initProtocol() {
        // Init protocol
        RightsManagementProtocol protocol = new RightsManagementProtocol(TestUtils.TEST_AUTH_SERVER,
            TestUtils.TEST_MANAGEMENT_SERVER, TestUtils.TEST_DEFAULT_JAVA_COURSE, TestUtils.TEST_DEFAULT_SEMESTER);
        String[] credentials = TestUtils.retreiveCredentialsFormVmArgs();
        try {
            protocol.login(credentials[0], credentials[1]);
        } catch (UnknownCredentialsException | ServerNotFoundException e) {
            Assertions.fail("Could not login for testing due to: " + e.getMessage(), e);
        }
        
        return protocol;
    }
}
