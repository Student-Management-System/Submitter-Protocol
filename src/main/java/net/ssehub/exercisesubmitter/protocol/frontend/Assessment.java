package net.ssehub.exercisesubmitter.protocol.frontend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.MarkerDto;
import net.ssehub.studentmgmt.backend_api.model.MarkerDto.SeverityEnum;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.ParticipantDto;

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
            assessment.getGroup().getMembers().stream()
                // TODO SE: Check if Displayname is required -> This would require an additional query!
                .map(p -> new User(p.getUsername(), p.getUsername(), p.getEmail()))
                .forEach(participants::add);
        } else {
            ParticipantDto participant = assessment.getParticipant();
            participants.add(new User(participant.getDisplayName(), participant.getUsername(), participant.getEmail()));
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
        return assignment.isGroupWork() ? assessment.getGroup().getName() : assessment.getParticipant().getUsername();
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
     * Clears the list of partial assessments.
     * Should be done before {@link #addAutomaticReview(String, String, String, String, String)} is called and only
     * if former assignments shall be deleted when submitting the assessment.
     * @param toolsToRemove The list of tool reports to delete.
     */
    public void clearPartialAssessments(String... toolsToRemove) {
        if (null != assessment.getPartialAssessments() && null != toolsToRemove && toolsToRemove.length > 0) {
            Set<String> removalList = new HashSet<>(Arrays.asList(toolsToRemove));
            assessment.getPartialAssessments().removeIf(p -> removalList.contains(p.getKey()));
        }
    }
    
    /**
     * Returns the number of partial assessments (provided by submission tools) related to this assessment.
     * @return The number of partial assessments (&ge; 0).
     */
    public int partialAsssesmentSize() {
        List<PartialAssessmentDto> partials = assessment.getPartialAssessments();
        return partials != null ? partials.size() : 0;
    }
    
    /**
     * Adds an partial assessment created as part of an (automatic) tool review.
     * An automatic report should at least contain the following information:
     * <ul>
     *   <li>The source/tool which created the report. A tool may create multiple reports.</li>
     *   <li>A message/synopsis explaining the report.</li>
     *   <li>A severity of the message/report.</li>
     * </ul>
     * Optionally, a report may add further details to locate the problematic part of the code:
     * <ul>
     *   <li>The file containing the problematic code.</li>
     *   <li>The line of containing the problematic code.</li>
     * </ul>
     * @param tool The tool (e.g. compiler, Junit, checkstyle, ...) which created the test
     * @param severity The severity of the review
     * @param message A detailed description of the review
     * @param file <b>Optional:</b> the file locating the problem of the review (doesn't work currently)
     * @param line <b>Optional:</b> the line inside the file locating the problem of the review (doesn't work currently)
     */
    public void addAutomaticReview(String tool, String severity, String message, String file, Integer line) {
        PartialAssessmentDto toolReview = null;
        boolean newPartial = false;
        
        if (null != assessment.getPartialAssessments()) {
            assessment.getPartialAssessments().stream()
                .filter(p -> p.getKey().equals(tool))
                .findFirst()
                .orElse(null);
        }
        
        if (null == toolReview) {
            toolReview = new PartialAssessmentDto();
            toolReview.setKey(tool);
            toolReview.setTitle(tool);
            toolReview.setDraftOnly(true);
            assessment.addPartialAssessmentsItem(toolReview);
            newPartial = true;
        }
        
        // Tool must not be null -> We use the tool as type and title
        if (null != file || null != line || null != severity) {
            MarkerDto marker = new MarkerDto();
            
            if (null != severity) {
                SeverityEnum severityType = SeverityEnum.fromValue(severity.toUpperCase());
                marker.setSeverity(severityType);
            }
            
            if (null != file) {
                marker.setPath(file);
            }
            
            if (null != line) {
                BigDecimal number = new BigDecimal(line);
                marker.setStartLineNumber(number);
                marker.setEndLineNumber(number);
            }

            if (null != message) {
                marker.setComment(message);
            }
            
            toolReview.addMarkersItem(marker);
        } else if (newPartial && null != message) {
            // New tool report without any marker -> store message here (dirty!)
            toolReview.comment(message);
        }
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
                .forEach(a -> printSummary(result, a));
        }
        
        return result.toString();
    }
    
    /**
     * ToString-method to create a nice summary for each marker of a partial assessment. If no markers are specified,
     * it will print only the name of the {@link PartialAssessmentDto}.
     * @param result The output (will be changed as side effect).
     * @param partial The {@link PartialAssessmentDto} to add to the <tt>result</tt>
     */
    private void printSummary(StringBuffer result, PartialAssessmentDto partial) {
        // Sort markers by severity
        List<MarkerDto> markers = partial.getMarkers();
        if (null != markers && !markers.isEmpty()) {
            Collections.sort(markers, (m1, m2) -> (-1 * m1.getSeverity().compareTo(m2.getSeverity())));
            for (MarkerDto marker : markers) {
                result.append(" - " + partial.getTitle());
                
                // Severity
                if (null != marker.getSeverity()) {
                    result.append(" (" + marker.getSeverity().name() + ")");
                }
                
                // Location
                if (null != marker.getPath()) {
                    result.append(" " + marker.getSeverity().name());    
                    if (null != marker.getStartLineNumber()) {
                        result.append(" " + marker.getStartLineNumber());                            
                        if (null != marker.getEndLineNumber()) {
                            result.append("-" + marker.getEndLineNumber());                            
                        }
                    }
                }
                
                // Synopsis
                if (null != marker.getComment()) {
                    result.append(":\t" + marker.getComment());    
                }
                
                result.append("\n");
            }
        } else {
            result.append(" - " + partial.getTitle() + "\n");
        }
        
        System.out.println(partial);
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
        if (partial1.getKey().equals(partial2.getKey())) {
            int severity1 = partial1.getMarkers().stream()
                .mapToInt(m -> m.getSeverity().ordinal())
                .max()
                .orElse(0);
            int severity2 = partial2.getMarkers().stream()
                .mapToInt(m -> m.getSeverity().ordinal())
                .max()
                .orElse(0);
            // Critical first -> Reverse order of ordinal definition
            result = -1 * Integer.compare(severity1, severity2);
        } else {
            result =  partial1.getKey().compareTo(partial2.getKey());                    
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
