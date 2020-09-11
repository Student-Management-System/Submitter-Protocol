package net.ssehub.exercisesubmitter.protocol.frontend;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.TestUtils;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.CollaborationEnum;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.StateEnum;

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
     * Creates an {@link RightsManagementProtocol} with default settings and logs in a tutor.
     * Useful for most tests.
     * @return {@link RightsManagementProtocol} usable for testing.
     */
    private RightsManagementProtocol initProtocol() {
        // Init protocol
        RightsManagementProtocol protocol = new RightsManagementProtocol(TestUtils.TEST_MANAGEMENT_SERVER,
                TestUtils.TEST_DEFAULT_JAVA_COURSE, TestUtils.TEST_DEFAULT_SEMESTER, TestUtils.retreiveAccessToken());
        
        return protocol;
    }
}
