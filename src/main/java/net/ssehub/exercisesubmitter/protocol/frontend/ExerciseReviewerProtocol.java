package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.ArrayList;
import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException;
import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;
import net.ssehub.studentmgmt.backend_api.model.AssessmentCreateDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentUpdateDto;
import net.ssehub.studentmgmt.backend_api.model.GroupDto;
import net.ssehub.studentmgmt.backend_api.model.UserDto;
import net.ssehub.studentmgmt.backend_api.model.UserDto.CourseRoleEnum;

/**
 * Network protocol that provides API calls as required by the <b>Eclipse Exercise Reviewer</b>.
 * Instances will store information (i.e., points) about the user and the assessment that is queried.
 * @author EL-Sharkawy
 * @author Kunold
 *
 */
public class ExerciseReviewerProtocol extends SubmitterProtocol {
    

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
        setNetworkComponents(null, new ReviewerProtocol(stdMgmtURL, courseName));
        assessments = new ArrayList<>();
    }
    
    @Override
    protected ReviewerProtocol getProtocol() {
        return (ReviewerProtocol) super.getProtocol();
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
        
        getProtocol().getAssessments(assignment.getID()).stream()
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
     * Submits the assessment (update/create) to the student management system.
     * <ul>
     *   <li>If the assessment exist on server: Assessment will be updated</li>
     *   <li>If the assessment exist not on server: Assessment will be created and local instance will be changed
     *   as side effect to store the ID created by the server</li>
     * </ul>
     * @param assessment A review to submit.
     * @return <tt>true</tt> if submission was successful, otherwise <tt>false</tt>.
     * @throws NetworkException when network problems occur.
     */
    public boolean submitAssessment(Assessment assessment) throws NetworkException {
        boolean assessmentExists = getProtocol().assessmentExists(assignment.getID(), assessment.getAssessmentID());
        boolean success = false;
        
        if (assessmentExists) {
            // Assessment exist -> Perform update
            AssessmentUpdateDto updateDto = new AssessmentUpdateDto();
            updateDto.setAchievedPoints(assessment.getAssessmentDTO().getAchievedPoints());
            updateDto.setComment(assessment.getAssessmentDTO().getComment());
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
                createDto.setUser(assessment.getAssessmentDTO().getUser());
            }
            String id = getProtocol().createAssessment(createDto, assessment.getAssignmentID());
            if (null != id) {
                assessment.getAssessmentDTO().setId(id);
                success = true;
            }
        }
        
        return success;
    }
    
    /**
     * Retrieves / creates an assessment for the group / user with the specified name.
     * It will create a new (empty) assessment, if there does not exist an assessment for the specified submitter.
     * <b style="color:red">Note:</b> Newly created assignments won't be uploaded to the server automatically.
     * Changes and new assessments must be uploaded via the {@link #submitAssessment(Assessment)} method.
     * @param name The name of the submitter (group name for group submissions, user account name (RZ name) for single
     *     user submissions).
     * @return An {@link Assessment} which may be used to review a submission
     * @see #submitAssessment(Assessment)
     */
    public Assessment getAssessmentForSubmission(String name) throws NetworkException {
        Assessment assessment = getAssessments().stream()
            .filter(a -> name.equals(a.getSubmitterName()))
            .findFirst()
            .orElse(createAssessment(name));
        
        return assessment;
    }
    
    /**
     * Creates a new blank {@link Assessment} and adds this to {@link #assessments}.
     * This {@link Assessment} object may be used to review an existent submission.
     * @param name The name of the submitter (group name for group submissions, user account name (RZ name) for single
     *     user submissions).
     * @return An {@link Assessment} which may be used to review a submission (will be added to {@link #assessments} as
     *     side effect).
     */
    private Assessment createAssessment(String name) throws NetworkException {
        AssessmentDto dto = new AssessmentDto();
        
        if (assignment.isGroupWork()) {
            GroupDto group = getProtocol().getGroupsAtAssignmentEnd(assignment.getID()).stream()
                .filter(g -> name.equals(g.getName()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("Could not find group '" + name + "'", name,
                    DataType.GROUP_NOT_FOUND));
            
            dto.setGroup(group);
            dto.setGroupId(group.getId());
        } else {
            UserDto user = getProtocol().getUsersOfCourse(CourseRoleEnum.STUDENT).stream()
                .filter(u -> name.equals(u.getRzName()))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("Could not find user '" + name + "'", name,
                    DataType.USER_NOT_FOUND));
            
            dto.setUser(user);
            dto.setUserId(user.getId());
        }
        
        Assessment assessment = new Assessment(dto, assignment);
        assessments.add(assessment);
        return assessment;
    }
    
    /**
     * Returns a formated String with all users and their points to an assignment.
     * @param assignmentId The ID of the assignment.
     * @return All users and their points to an assignment.
     */
    public String getSubmissionRealUsersReviews(String assignmentId) {
        String userReviews = "null";
        
        try {
            userReviews = getProtocol().getSubmissionRealUsersReviews(assignmentId);
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
            submissionUsers = getProtocol().getSubmissionReviewerUsers(assignmentId);
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
            submissionReviews = getProtocol().getSubmissionReviews(assignmentId);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        
        return submissionReviews;
    }
    
}
