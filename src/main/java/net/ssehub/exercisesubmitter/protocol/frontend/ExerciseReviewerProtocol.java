package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;

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
    
    private ReviewerProtocol protocol;
    private Assignment assignment;
    
    
    /**
     * Creates a new {@link ReviewerProtocol} instance for a specific course.
     * @param stdMgmtURL The URL of the student management service
     * @param courseName The course that is associated with the exercise submitter.
     */
    public ExerciseReviewerProtocol(String stdMgmtURL, String courseName) {
        protocol = new ReviewerProtocol(stdMgmtURL, courseName);
    }
    
    /**
     * Returns a formated String with all users and their points to an assignment.
     * @param assignmentId The ID of the assignment.
     * @return All users and their points to an assignment.
     */
    public String getSubmissionRealUsersReviews(String assignmentId) {
        String userReviews;
        List<AssessmentDto> assessment = null;
        try {
            assessment = protocol.getAssessments(assignmentId);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        //first line: user  taskname    taskname    taskname
        userReviews = USER + SEPARATOR + assignment.getName() + SEPARATOR + assignment.getName() + SEPARATOR 
                + assignment.getName() + LINE_END;
        //second line: *max*    points  points  points
        userReviews += MAX_POINTS + SEPARATOR + assignment.getName() + SEPARATOR + assignment.getName() + SEPARATOR 
                + assignment.getName() + LINE_END; //TODO: needs getter for assignment points
        
        for (AssessmentDto ass : assessment) {
            //TODO: needs user name instead of id
            userReviews += ass.getUserId() + SEPARATOR + ass.getAchievedPoints() + SEPARATOR + ass.getAchievedPoints()
                + SEPARATOR + ass.getAchievedPoints() + LINE_END;
        }
        
        return userReviews;
    }
    
}
