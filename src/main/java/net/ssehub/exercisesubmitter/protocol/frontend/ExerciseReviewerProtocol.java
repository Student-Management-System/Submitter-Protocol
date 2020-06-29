package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.UserDto;

/**
 * Network protocol that provides API calls as required by the <b>Eclipse Exercise Reviewer</b>.
 * Instances will store information (i.e., points) about the user and the assessment that is queried.
 * @author Kunold
 *
 */
public class ExerciseReviewerProtocol {

    private static final String USER = "user";
    private static final String MAX_POINTS = "*max*";
    private static final String SEPARATOR = "\t";
    private static final String LINE_END = "\n";
    
    private ReviewerProtocol rp;
    private Assignment assignment;
    
    
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
        String userReviews;
        List<AssessmentDto> assessments = null;
        try {
            assessments = rp.getAssessments(assignmentId);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        //first line: user  taskname    taskname    taskname
        userReviews = USER + SEPARATOR + assignment.getName() + SEPARATOR + assignment.getName() + SEPARATOR 
                + assignment.getName() + LINE_END;
        //second line: *max*    points  points  points
        userReviews += MAX_POINTS + SEPARATOR + assignment.getPoints() + SEPARATOR + assignment.getPoints() + SEPARATOR 
                + assignment.getPoints() + LINE_END;
        
        // vollername   punkte  bewertung   upload erfolgreich(momentan nicht abrufbar)
        for (AssessmentDto assessment : assessments) {
            for (UserDto user : assessment.getGroup().getUsers()) {
                userReviews += user.getUsername() + SEPARATOR + assessment.getAchievedPoints() + SEPARATOR 
                        + assessment.getComment() + LINE_END;
            }
        }
        
        return userReviews;
    }
    
    /**
     * Returns a formated String with all groups and their users.
     * @param assignmentId The ID of the assignment.
     * @return All users whose submission is reviewed.
     */
    public String getSubmissionReviewerUsers(String assignmentId) {
        String submissionUsers = "";
        List<AssessmentDto> assessments = null;
        
        try {
            assessments = rp.getAssessments(assignmentId);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        
        //gruppenname   vollername    rz-kennung  uni-mail
        for (AssessmentDto assessment : assessments) {
            for (UserDto user : assessment.getGroup().getUsers()) {
                submissionUsers += assessment.getGroup().getName() + SEPARATOR + user.getUsername() + SEPARATOR 
                        + user.getRzName() + SEPARATOR + user.getEmail() + LINE_END;
            }
        }
        
        return submissionUsers;
    }
    
    /**
     * Returns a formated String with all groups and there review.
     * @param assignmentId The ID of the assignment.
     * @return All groups whose submission is reviewed.
     */
    public String getSubmissionReviews(String assignmentId) {
        String submissionReviews;
        List<AssessmentDto> assessments = null;
        
        try {
            assessments = rp.getAssessments(assignmentId);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        
        //first line: user  taskname    taskname    taskname
        submissionReviews = USER + SEPARATOR + assignment.getName() + SEPARATOR + assignment.getName() + SEPARATOR 
                + assignment.getName() + LINE_END;
        //second line: *max*    points  points  points
        submissionReviews += MAX_POINTS + SEPARATOR + assignment.getPoints() + SEPARATOR + assignment.getPoints() 
                + SEPARATOR + assignment.getPoints() + LINE_END;
        
        //gruppenname   punkte  kommentar   upload erfolgreich
        for (AssessmentDto assessment : assessments) {
            submissionReviews += assessment.getGroup().getName() + SEPARATOR + assessment.getAchievedPoints() 
                    + SEPARATOR + assessment.getComment() + SEPARATOR + assessment.getId() + LINE_END;
        }
        
        return submissionReviews;
    }
    
}
