package net.ssehub.exercisesubmitter.protocol.frontend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto.SeverityEnum;
import net.ssehub.studentmgmt.backend_api.model.UserDto;

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
        Assignment assignment = new Assignment("Test", "AssignmentID 1", State.SUBMISSION, false);
        UserDto user = new UserDto();
        user.setUsername("a user");
        user.setEmail("a@mail.com");
        user.setUsername("auser");
        
        AssessmentDto dto = new AssessmentDto();
        dto.setId("AssessmentID 1");
        dto.setUser(user);
        
        PartialAssessmentDto partial1 = new PartialAssessmentDto();
        partial1.setAssessmentId("pAssessment ID 1");
        partial1.comment("This is problematic");
        partial1.setType("Compiler");
        partial1.setSeverity(SeverityEnum.WARNING);
        dto.addPartialAssessmentsItem(partial1);
        
        PartialAssessmentDto partial2 = new PartialAssessmentDto();
        partial2.setAssessmentId("pAssessment ID 2");
        partial2.comment("This is wrong");
        partial2.setType("JUnit");
        partial2.setSeverity(SeverityEnum.ERROR);
        dto.addPartialAssessmentsItem(partial2);
        
        PartialAssessmentDto partial3 = new PartialAssessmentDto();
        partial3.setAssessmentId("pAssessment ID 3");
        partial3.comment("This is wrong");
        partial3.setType("Compiler");
        partial3.setSeverity(SeverityEnum.ERROR);
        dto.addPartialAssessmentsItem(partial3);
        
        Assessment assessment = new Assessment(dto, assignment);
        String[] summary = assessment.summerizePartialAssessments().split("\n");
        assertPartialSummary(summary[0], partial3); // 1st: Compiler Error
        assertPartialSummary(summary[1], partial1); // 2nd: Compiler Warning
        assertPartialSummary(summary[2], partial2); // 3rd: Junit
    }
    
    /**
     * Tests that {@link Assessment#partialAsssesmentSize()} returns valid values.
     */
    @Test
    public void testPartialAsssesmentSize() {
        Assignment assignment = new Assignment("Test", "AssignmentID 1", State.SUBMISSION, false);
        UserDto user = new UserDto();
        user.setUsername("a user");
        user.setEmail("a@mail.com");
        user.setUsername("auser");
        
        AssessmentDto dto = new AssessmentDto();
        dto.setId("AssessmentID 1");
        dto.setUser(user);
        
        Assessment assessment = new Assessment(dto, assignment);
        
        // Check that there is no NullPointerException thrown
        Assertions.assertEquals(0, assessment.partialAsssesmentSize());
        
        // Add one partial assessment
        PartialAssessmentDto partial1 = new PartialAssessmentDto();
        partial1.setAssessmentId("pAssessment ID 1");
        partial1.comment("This is problematic");
        partial1.setType("Compiler");
        partial1.setSeverity(SeverityEnum.WARNING);
        dto.addPartialAssessmentsItem(partial1);
        
        Assertions.assertEquals(1, assessment.partialAsssesmentSize());
    }
    
    /**
     * Asserts that the {@link PartialAssessmentDto} is correctly transformed into a string.
     * @param summary The expected summary
     * @param partial The {@link PartialAssessmentDto} to be transformed
     */
    private void assertPartialSummary(String summary, PartialAssessmentDto partial) {
        String expected = " - " + partial.getType() + " (" + partial.getSeverity().name() + "):\t"
            + partial.getComment(); 
        Assertions.assertEquals(expected, summary);
    }
}
