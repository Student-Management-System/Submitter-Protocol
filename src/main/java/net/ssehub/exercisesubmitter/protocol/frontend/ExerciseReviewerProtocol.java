package net.ssehub.exercisesubmitter.protocol.frontend;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;

/**
 * Network protocol that provides API calls as required by the <b>Eclipse Exercise Reviewer</b>.
 * Instances will store information (i.e., points) about the user and the assessment that is queried.
 * @author Kunold
 *
 */
public class ExerciseReviewerProtocol {
    
    private ReviewerProtocol rp;
    
    
    /**
     * Creates a new {@link ReviewerProtocol} instance for a specific course.
     * @param stdMgmtURL The URL of the student management service
     * @param courseName The course that is associated with the exercise submitter.
     */
    public ExerciseReviewerProtocol(String stdMgmtURL, String courseName) {
        rp = new ReviewerProtocol(stdMgmtURL, courseName);
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
