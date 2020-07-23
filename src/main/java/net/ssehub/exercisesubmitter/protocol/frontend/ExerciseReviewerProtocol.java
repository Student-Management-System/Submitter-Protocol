package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.ArrayList;
import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;

/**
 * Network protocol that provides API calls as required by the <b>Eclipse Exercise Reviewer</b>.
 * Instances will store information (i.e., points) about the user and the assessment that is queried.
 * @author EL-Sharkawy
 * @author Kunold
 *
 */
public class ExerciseReviewerProtocol extends SubmitterProtocol {
    
    private ReviewerProtocol rp;

    private List<Assessment> assessments;
    private Assignment assignment;
    
    /**
     * Creates a new {@link ReviewerProtocol} instance for a specific course.
     * @param authenticationURL The URL of the authentication server (aka Sparky service)
     * @param stdMgmtURL The URL of the student management service
     * @param courseName The course that is associated with the exercise submitter.
     * @param submissionServer The root (URL) where to submit assignments (exercises).
     */
    public ExerciseReviewerProtocol(String authenticationURL, String stdMgmtURL, String courseName,
        String submissionServer) {
        
        super(authenticationURL, stdMgmtURL, courseName, submissionServer);
        rp = new ReviewerProtocol(stdMgmtURL, courseName);
        assessments = new ArrayList<>();
    }
    
    /**
     * Loads the initial list of (empty) assessments from the server, which may be edited and uploaded to assess
     * the submissions of the course attendees.
     * If there was no assessment uploaded before,
     * the list may be empty or misses assessments of a specific group/attendee.
     * @param assignment The assignment which is currently reviewed.
     * @throws NetworkException If <b>Student Management Server</b> cannot be queried
     *     or the user is no tutor of the course.
     */
    public void loadAssessments(Assignment assignment) throws NetworkException {
        assessments.clear();
        this.assignment = assignment;
        
        rp.getAssessments(assignment.getID()).stream()
            .map(a -> new Assessment(a, assignment))
            .forEach(assessments::add);
    }
    
    /**
     * Returns the list of currently edited/reviews {@link Assessment}s.
     * Requires to be loaded from server first, via {@link #loadAssessments(Assignment)}.
     * @return All assessments for the currently reviewed {@link Assignment}.
     * @see #loadAssessments(Assignment)
     */
    public List<Assessment> getAssessments() {
        return assessments;
    }
    
    /**
     * Returns a formated String with all users and their points to an assignment.
     * @param assignmentId The ID of the assignment.
     * @return All users and their points to an assignment.
     */
    public String getSubmissionRealUsersReviews(String assignmentId) {
        String userReviews = "null";
        
        try {
            userReviews = rp.getSubmissionRealUsersReviews(assignmentId);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        
        return userReviews;
    }
    
    /**
     * Returns a formated String with all groups and their users.
     * @param assignmentId The ID of the assignment.
     * @return All users whose submission is reviewed.
     */
    public String getSubmissionReviewerUsers(String assignmentId) {
        String submissionUsers = "null";
        
        try {
            submissionUsers = rp.getSubmissionReviewerUsers(assignmentId);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        
        return submissionUsers;
    }
    
    /**
     * Returns a formated String with all groups and there review.
     * @param assignmentId The ID of the assignment.
     * @return All groups whose submission is reviewed.
     */
    public String getSubmissionReviews(String assignmentId) {
        String submissionReviews = "null";
        
        try {
            submissionReviews = rp.getSubmissionReviews(assignmentId);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        
        return submissionReviews;
    }
    
}
