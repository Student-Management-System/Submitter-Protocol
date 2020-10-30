package net.ssehub.exercisesubmitter.protocol.frontend;

import java.math.BigDecimal;

import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;

/**
 * Review protocol for the SVN submission hook that automatically reviews student submissions and upload the results
 * to the <b>student management system</b>.
 * @author El-Sharkawy
 *
 */
public class SubmissionHookProtocol extends AbstractReviewerProtocol {

    /**
     * Creates a new {@link SubmissionHookProtocol} instance for a specific course.
     * @param authenticationURL The URL of the authentication server (aka Sparky service)
     * @param stdMgmtURL The URL of the student management service
     * @param courseName The course that is associated with the exercise submitter.
     * @param submissionServer The root (URL) where to submit assignments (exercises).
     */
    public SubmissionHookProtocol(String authenticationURL, String stdMgmtURL, String courseName,
        String submissionServer) {
        
        super(authenticationURL, stdMgmtURL, courseName, submissionServer);
    }
    
    /**
     * Returns the assignment with the specified name.
     * This method does not care about the state of the assignments to avoid errors if an assignment changes its
     * state during the submission.
     * @param assignmentName The name of the assignment to search for.
     * @return The assignment with the specified name.
     * @throws DataNotFoundException If there was no assignment with the specified name.
     * @throws NetworkException If network problems occur
     */
    public Assignment getAssignmentByName(String assignmentName) throws NetworkException {
        Assignment assignment = getProtocol().getAssignments().stream()
            .filter(a -> assignmentName.equals(a.getName()))
            .findAny()
            .orElseThrow(() -> new DataNotFoundException("No assignment was found with the specified name '"
                + assignmentName + "'.", assignmentName, DataNotFoundException.DataType.ASSIGNMENTS_NOT_FOUND));
        
        return assignment;
    }
    
    /**
     * Loads (or creates) an {@link Assessment} object for a given submission to review this submission.
     * This method searches for an existing assessment on the server and returns this object if found or creates a new
     * blank element which can be filled and submitted.
     * @param assignment The assignment (exercise, homework, exam)
     * @param submitterName The name of the submitter (group name for group submissions, user account name (RZ name) for
     *     single user submissions).
     * 
     * @return A (potentially blank) assessment, which may be used to create and submit a review.
     * @throws NetworkException When network problems occur.
     */
    public Assessment loadAssessmentByName(Assignment assignment, String submitterName) throws NetworkException {
        /*
         * Double check to retrieve that assessment that belongs to submission:
         * By passing groupName name to backend API, server filters for the name allowing similar names
         * No use second filter to restrict it to exact match.
         */
        AssessmentDto assessmentDto = getProtocol().getAssessments(assignment.getID(), submitterName).stream()
            .filter(a -> (assignment.isGroupWork() && submitterName.equals(a.getGroup().getName()))
                    || (!assignment.isGroupWork() && submitterName.equals(a.getParticipant().getUsername())))
            .findAny()
            .orElse(null);
        
        Assessment assessment;
        if (null != assessmentDto) {
            // Convert dto into frontend object
            assessment = new Assessment(assessmentDto, assignment);
        } else {
            // In case there wasn't a previous assessment, there won't be a valid match and a new one must be created.

            // First check if submitter exists
            if (assignment.isGroupWork()) {
                if (!super.groupExists(submitterName, assignment.getID())) {
                    throw new DataNotFoundException("No group registered with the specified group name: "
                        + submitterName, submitterName, DataType.GROUP_NOT_FOUND);
                }
            } else {
                if (!super.studentExists(submitterName)) {
                    throw new DataNotFoundException("No user registered with the specified user name: " + submitterName,
                        submitterName, DataType.USER_NOT_FOUND);
                }
            }
            
            assessment = createAssessment(assignment, submitterName);
        }
        
        return assessment;
    }
    
    @Override
    public boolean submitAssessment(Assignment assignment, Assessment assessment) throws NetworkException {
        if (null == assessment.getAssessmentDTO().getAchievedPoints() && assignment.getState() == State.SUBMISSION) {
            // Assessments do not have points during submission -> Set to 0 points to avoid exception on server
            assessment.getAssessmentDTO().setAchievedPoints(new BigDecimal(0));
        }
        return super.submitAssessment(assignment, assessment);
    }

}
