package net.ssehub.exercisesubmitter.protocol.frontend;

import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;

/**
 * Stores data related to an assessment of a submitted assignment.
 * @author El-Sharkawy
 *
 */
public class Assessment {
    
    /**
     * Reference to the assignment (name of the exercise, points, ID for queries, ...).
     */
    private Assignment assignment;
    
    private AssessmentDto assessment;
    
    /**
     * Creates a new {@link Assessment} instance storing the review of an assignment for one submission.
     * @param dto The dto which stores the full and partial assessments for one submission
     * @param assignment The assignment specification for the submission
     */
    public Assessment(AssessmentDto dto, Assignment assignment) {
        this.assignment = assignment;
        this.assessment = dto;
    }

    /**
     * Returns the ID of the assessment to query the REST server, should not be used by the submitter/reviewer directly.
     * @return The ID to query the REST server for additional information.
     */
    protected String getAssessmentID() {
        return assessment.getId();
    }
    
    /**
     * Returns the ID of the assignment to query the REST server, should not be used by the submitter/reviewer directly.
     * @return The ID to query the REST server for additional information.
     */
    protected String getAssignmentID() {
        return assignment.getID();
    }
    
    /**
     * Returns the unique name of the related group or student.
     * @return In case of a group work the group name, the user account name (RZ name) otherwise.
     */
    protected String getSubmitterName() {
        return assignment.isGroupWork() ? assessment.getGroup().getName() : assessment.getUser().getRzName();
    }
    
    /**
     * Returns the full/complete review comment.
     * @return The comment/description of the review.
     */
    public String getFullReviewComment() {
        return assessment.getComment();
    }
}
