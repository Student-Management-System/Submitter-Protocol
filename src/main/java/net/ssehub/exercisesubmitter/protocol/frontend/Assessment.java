package net.ssehub.exercisesubmitter.protocol.frontend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;
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
                .map(u -> new User(u.getUsername(), u.getRzName(), u.getEmail()))
                .forEach(participants::add);
        } else {
            UserDto userDto = assessment.getUser();
            participants.add(new User(userDto.getUsername(), userDto.getRzName(), userDto.getEmail()));
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
    
    /**
     * Grants full access to the DTO object which is used to transfer (down- and upload) all (partial) review data of
     * one submission.
     * @return The assessment dto.
     */
    protected AssessmentDto getAssessmentDTO() {
        return assessment;
    }
    
    /**
     * Returns the unique name of the related group or student.
     * @return In case of a group work the group name, the user account name (RZ name) otherwise.
     */
    public String getSubmitterName() {
        return assignment.isGroupWork() ? assessment.getGroup().getName() : assessment.getUser().getRzName();
    }
    
    /**
     * Returns the full/complete review comment.
     * @return The comment/description of the review.
     */
    public String getFullReviewComment() {
        return assessment.getComment();
    }
    
    /**
     * Sets the review comment / synopsis.
     * This does not affect any partial assignments given by review tools automatically.
     * @param reviewComment The comment/description of the review.
     */
    public void setFullReviewComment(String reviewComment) {
        assessment.setComment(reviewComment);
    }
    
    /**
     * Returns the achieved points.
     * @return The number of achieved points for the submissions, <tt>0</tt> if not specified so far.
     */
    public Double getAchievedPoints() {
        return assessment.getAchievedPoints() != null ? assessment.getAchievedPoints().doubleValue() : 0;
    }
    
    /**
     * Sets the number of achieved points.
     * @param points The number of achieved points for the submissions.
     */
    public void setAchievedPoints(double points) {
        assessment.setAchievedPoints(new BigDecimal(points));
    }
    
    /**
     * Returns the number of partial assessments (provided by submission tools) related to this assessment.
     * @return The number of partial assessments (&ge; 0).
     */
    public int partialAsssesmentSize() {
        List<PartialAssessmentDto> partials = assessment.getPartialAssessments();
        return partials != null ? partials.size() : null;
    }
    
    /**
     * Returns the partial assessment at the specified position.
     *
     * @param index A 0-based index of the assessment to return.
     * @return the partial assessment at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range (<tt>index < 0 || index >= 
     *     {@link #partialAsssesmentSize()} </tt>)
     * @see {@link #partialAsssesmentSize()}
     */
    public PartialAssessmentDto getPartialAssessment(int index) {
        return assessment.getPartialAssessments().get(index);
    }
    
    /**
     * Returns a summary in list format of the partial assessments as a string.
     * @return A bullet list of the partial assessments of an empty String if there is no partial assessment available.
     */
    public String summerizePartialAssessments() {
        StringBuffer result = new StringBuffer();
        
        if (partialAsssesmentSize() > 0) {
            assessment.getPartialAssessments().stream()
                .sorted(Assessment::compare)
                .map(a -> " - " + a.getType() + " (" + a.getSeverity() + "):\t" + a.getComment() + "\n")
                .forEach(result::append);
        }
        
        return result.toString();
    }
    
    /**
     * Sorting method to sort {@link PartialAssessmentDto}s.
     * Sorts first be the tool/type and if they are equal then by their severity (cirtical first)
     * @param partial1 The first element so sort
     * @param partial2 The second element to sort
     * @return  the value <tt>0</tt> if both elements are equal;
     *     a value less than <tt>0</tt> if <tt>partial1</tt> is smaller than <tt>partial2</tt>;
     *     and a value greater than <tt>0</tt> <tt>partial1</tt> is bigger than <tt>partial2</tt>;
     */
    private static int compare(PartialAssessmentDto partial1, PartialAssessmentDto partial2) {
        int result;
        if (partial1.getType().equals(partial2.getType())) {
            // Critical first -> Reverse order of ordinal definition
            result = -1 * partial1.getSeverity().compareTo(partial2.getSeverity());
        } else {
            result =  partial1.getType().compareTo(partial2.getType());                    
        }
        
        return result;
    }
    
    @Override
    public Iterator<User> iterator() {
        return participants.iterator();
    }
    
    @Override
    public String toString() {
        return "Assessment '" + assignment.getName() + "' of " + getSubmitterName();
    }
}
