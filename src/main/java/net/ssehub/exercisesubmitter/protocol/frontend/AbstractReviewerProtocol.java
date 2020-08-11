package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.Collection;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;
import net.ssehub.studentmgmt.backend_api.model.AssessmentCreateDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentUpdateDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;

/**
 * Super class for reviewing tools. This class stores functionalities that are used by more than one reviewing tool.
 * @author El-Sharkawy
 *
 */
abstract class AbstractReviewerProtocol extends SubmitterProtocol {

    /**
     * Creates a new {@link AbstractReviewerProtocol} instance for a specific course.
     * @param authenticationURL The URL of the authentication server (aka Sparky service)
     * @param stdMgmtURL The URL of the student management service
     * @param courseName The course that is associated with the exercise submitter.
     * @param submissionServer The root (URL) where to submit assignments (exercises).
     */
    protected AbstractReviewerProtocol(String authenticationURL, String stdMgmtURL, String courseName,
        String submissionServer) {
        
        super(authenticationURL, stdMgmtURL, courseName, submissionServer);
        setNetworkComponents(null, new ReviewerProtocol(stdMgmtURL, courseName));
    }
    
    @Override
    protected ReviewerProtocol getProtocol() {
        return (ReviewerProtocol) super.getProtocol();
    }
    
    /**
     * Submits the assessment (update/create) to the student management system.
     * <ul>
     *   <li>If the assessment exist on server: Assessment will be updated</li>
     *   <li>If the assessment exist not on server: Assessment will be created and local instance will be changed
     *   as side effect to store the ID created by the server</li>
     * </ul>
     * @param assignment The assignment (exercise, homework, exam) for which a submission was retrieved and reviewed
     * @param assessment A review to submit.
     * @return <tt>true</tt> if submission was successful, otherwise <tt>false</tt>.
     * @throws NetworkException when network problems occur.
     */
    protected boolean submitAssessment(Assignment assignment, Assessment assessment) throws NetworkException {
        boolean assessmentExists = getProtocol().assessmentExists(assignment.getID(), assessment.getAssessmentID());
        boolean success = false;
        
        if (assessmentExists) {
            // Assessment exist -> Perform update
            AssessmentUpdateDto updateDto = new AssessmentUpdateDto();
            updateDto.setAchievedPoints(assessment.getAssessmentDTO().getAchievedPoints());
            updateDto.setComment(assessment.getAssessmentDTO().getComment());
            
            // Remove obsolete partial assessments
            Collection<PartialAssessmentDto> partialsToRemove = assessment.getRemovedPartialAssessments();
            if (null != partialsToRemove) {
                for (PartialAssessmentDto partialAssessmentDto : partialsToRemove) {
                    updateDto.addRemovePartialAssignmentsItem(partialAssessmentDto);
                }
            }
            
            // Add new partial assessments
            for (int i = 0; i < assessment.partialAsssesmentSize(); i++) {
                // Check that assessment doesn't belong to downloaded partials -> has no ID given by the server
                if (assessment.getPartialAssessment(i).getId() != null) {
                    updateDto.addAddPartialAssessmentsItem(assessment.getPartialAssessment(i));
                }
            }

            success = getProtocol().updateAssessment(updateDto, assignment.getID(), assessment.getAssessmentID());
        } else {
            // Assessment does not exist -> Create new assessment
            AssessmentCreateDto createDto = new AssessmentCreateDto();
            createDto.setAssignmentId(assessment.getAssignmentID());
            createDto.setComment(assessment.getFullReviewComment());
            createDto.setAchievedPoints(assessment.getAssessmentDTO().getAchievedPoints());
            createDto.setCreatorId(getUserID());
            if (assignment.isGroupWork()) {
                createDto.setGroupId(assessment.getAssessmentDTO().getGroupId());
            } else {
                createDto.setUserId(assessment.getAssessmentDTO().getUserId());
            }
            
            // Add new partial assessments
            for (int i = 0; i < assessment.partialAsssesmentSize(); i++) {
                createDto.addPartialAssessmentsItem(assessment.getPartialAssessment(i));
            }
            
            String id = getProtocol().createAssessment(createDto, assessment.getAssignmentID());
            if (null != id) {
                assessment.getAssessmentDTO().setId(id);
                success = true;
            }
        }
        
        return success;
    }

}
