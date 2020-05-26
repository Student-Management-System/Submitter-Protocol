package net.ssehub.exercisesubmitter.protocol.frontend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.ssehub.exercisesubmitter.protocol.backend.LoginComponent;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkProtocol;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.CollaborationEnum;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.StateEnum;

/**
 * Tests the {@link SubmitterProtocol} <b>without</b> querying the REST server.
 * @author El-Sharkawy
 *
 */
public class SubmitterProtocolUnitTests {
    
    /**
     * Test correct computation of destination path of <b>Single</b> {@link Assignment}s via
     * {@link SubmitterProtocol#getPathToSubmission(Assignment)}.
     * @throws NetworkException Must not occur, is not tested and network is not used. If this occur, internal API of
     *     {@link SubmitterProtocol} has been changed.
     */
    @Test
    public void testGetPathToSubmissionForSingleSubmission() throws NetworkException {
        // Test data
        String expectedExercise = "Exercise";
        String expectedUserOrGroup = "user";
        
        // Single assignment
        AssignmentDto dto = new AssignmentDto();
        dto.setName(expectedExercise);
        dto.setCollaboration(CollaborationEnum.SINGLE);
        dto.setState(StateEnum.IN_PROGRESS);
        Assignment assignment = new Assignment(dto);
        
        // Mock of REST calls
        LoginComponent loginMock = Mockito.mock(LoginComponent.class);
        Mockito.when(loginMock.getUserName()).thenReturn(expectedUserOrGroup);
        SubmitterProtocol protocol = new SubmitterProtocol(null, null, null, null);
        protocol.setNetworkComponents(loginMock, null);
        
        // Test: Correct computation of destination path
        String[] path = protocol.getPathToSubmission(assignment);
        Assertions.assertArrayEquals(new String[] {expectedExercise, expectedUserOrGroup}, path);
    }
    
    /**
     * Test correct computation of destination path of <b>Group</b> {@link Assignment}s via
     * {@link SubmitterProtocol#getPathToSubmission(Assignment)}.
     * @throws NetworkException Must not occur, is not tested and network is not used. If this occur, internal API of
     *     {@link SubmitterProtocol} has been changed.
     */
    @Test
    public void testGetPathToSubmissionForGroupSubmission() throws NetworkException {
        // Test data
        String expectedExercise = "Exercise";
        String expectedUserOrGroup = "group";
        String usedAssignmentID = "123";
        
        // Single assignment
        AssignmentDto dto = new AssignmentDto();
        dto.setName(expectedExercise);
        dto.setCollaboration(CollaborationEnum.GROUP);
        dto.setState(StateEnum.IN_PROGRESS);
        dto.setId(usedAssignmentID);
        Assignment assignment = new Assignment(dto);
        
        // Mock of REST calls
        NetworkProtocol networkMock = Mockito.mock(NetworkProtocol.class);
        Mockito.when(networkMock.getGroupForAssignment(Mockito.any(), Mockito.anyString()))
            .thenReturn(expectedUserOrGroup);
        SubmitterProtocol protocol = new SubmitterProtocol(null, null, null, null);
        protocol.setNetworkComponents(null, networkMock);
        
        // Test: Correct computation of destination path
        String[] path = protocol.getPathToSubmission(assignment);
        Assertions.assertArrayEquals(new String[] {expectedExercise, expectedUserOrGroup}, path);
    }

}