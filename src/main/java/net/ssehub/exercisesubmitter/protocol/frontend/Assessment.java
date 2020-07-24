package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.UserDto;

/**
 * Stores data related to an assessment of a submitted assignment.
 * @author El-Sharkawy
 *
 */
public class Assessment implements Iterable<User> {
    
    /**
     * Reference to the assignment (name of the exercise, points, ID for queries, ...).
     */
    private Assignment assignment;
    
    private AssessmentDto assessment;
    
    private List<User> participants;
    
    /**
     * Creates a new {@link Assessment} instance storing the review of an assignment for one submission.
     * @param dto The dto which stores the full and partial assessments for one submission
     * @param assignment The assignment specification for the submission
     */
    public Assessment(AssessmentDto dto, Assignment assignment) {
        this.assignment = assignment;
        this.assessment = dto;
        loadUsersOfAssessment();
    }
    
    /**
     * Loads the {@link User}s that belong to the assessment.
     */
    private void loadUsersOfAssessment() {
        List<User> participants = new ArrayList<>();
        if (assignment.isGroupWork()) {
            assessment.getGroup().getUsers().stream()
                .map(u -> new User(u.getUsername(), u.getRzName()))
                .forEach(participants::add);
        } else {
            UserDto userDto = assessment.getUser();
            participants.add(new User(userDto.getUsername(), userDto.getRzName()));
        }
        
        this.participants = Collections.unmodifiableList(participants);
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
    
//    /**
//     * Grants full access to the DTO object which is used to transfer (down- and upload) all (partial) review data of
//     * one submission.
//     * @return The assessment dto.
//     */
//    protected AssessmentDto getAssessmentDTO() {
//        return assessment;
//    }
    
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

    @Override
    public Iterator<User> iterator() {
        return participants.iterator();
    }
}
