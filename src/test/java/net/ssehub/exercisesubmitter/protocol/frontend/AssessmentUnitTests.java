package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.MarkerDto;
import net.ssehub.studentmgmt.backend_api.model.MarkerDto.SeverityEnum;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.ParticipantDto;

/**
 * Tests the correct behavior of the {@link Assessment}, which doesn't need any integration to remote server
 * for testing.
 * @author El-Sharkawy
 *
 */
public class AssessmentUnitTests {

    /**
     * Tests the correct creation (especially ordering) of {@link Assessment#summerizePartialAssessments()}.
     */
    @Test
    public void testSummerizePartialAssessments() {
        Assignment assignment = new Assignment("Test", "AssignmentID 1", State.SUBMISSION, false, 0);
        ParticipantDto user = new ParticipantDto();
        user.setUsername("a user");
        user.setEmail("a@mail.com");
        user.setUsername("auser");
        
        AssessmentDto dto = new AssessmentDto();
        dto.setId("AssessmentID 1");
        dto.setParticipant(user);
        
        PartialAssessmentDto partial1 = new PartialAssessmentDto();
        partial1.comment("Compiler");
        partial1.setKey("Compiler");
        partial1.setTitle("Compiler");
        MarkerDto marker1 = new MarkerDto();
        marker1.setSeverity(SeverityEnum.WARNING);
        marker1.comment("This is problematic");
        
        MarkerDto marker2 = new MarkerDto();
        marker2.setSeverity(SeverityEnum.ERROR);
        marker2.comment("This is a compilation error");
        
        partial1.setMarkers(Arrays.asList(marker1, marker2));
        dto.addPartialAssessmentsItem(partial1);
        
        PartialAssessmentDto partial2 = new PartialAssessmentDto();
        partial2.comment("JUnit");
        partial2.setKey("JUnit");
        partial2.setTitle("JUnit");
        MarkerDto marker3 = new MarkerDto();
        marker3.setSeverity(SeverityEnum.ERROR);
        marker3.comment("This is a JUnit error");
        
        partial2.setMarkers(Arrays.asList(marker3));
        dto.addPartialAssessmentsItem(partial2);
        
        Assessment assessment = new Assessment(dto, assignment);
        String[] summary = assessment.summerizePartialAssessments().split("\n");
        Assertions.assertEquals(" - Compiler (ERROR):\tThis is a compilation error", summary[0]);
        Assertions.assertEquals(" - Compiler (WARNING):\tThis is problematic", summary[1]);
        Assertions.assertEquals(" - JUnit (ERROR):\tThis is a JUnit error", summary[2]);
    }
    
    /**
     * Tests that {@link Assessment#partialAsssesmentSize()} returns valid values.
     */
    @Test
    public void testPartialAsssesmentSize() {
        Assignment assignment = new Assignment("Test", "AssignmentID 1", State.SUBMISSION, false, 0);
        ParticipantDto user = new ParticipantDto();
        user.setUsername("a user");
        user.setEmail("a@mail.com");
        user.setUsername("auser");
        
        AssessmentDto dto = new AssessmentDto();
        dto.setId("AssessmentID 1");
        dto.setParticipant(user);
        
        Assessment assessment = new Assessment(dto, assignment);
        
        // Check that there is no NullPointerException thrown
        Assertions.assertEquals(0, assessment.partialAsssesmentSize());
        
        // Add one partial assessment
        PartialAssessmentDto partial1 = new PartialAssessmentDto();
        partial1.comment("This is problematic");
        dto.addPartialAssessmentsItem(partial1);
        
        Assertions.assertEquals(1, assessment.partialAsssesmentSize());
    }
    
}
