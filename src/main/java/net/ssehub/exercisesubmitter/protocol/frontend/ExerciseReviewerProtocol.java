package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.exercisesubmitter.protocol.backend.ReviewerProtocol;
import net.ssehub.studentmgmt.backend_api.model.GroupDto;
import net.ssehub.studentmgmt.backend_api.model.ParticipantDto.RoleEnum;

/**
 * Network protocol that provides API calls as required by the <b>Eclipse Exercise Reviewer</b>.
 * Instances will store information (i.e., points) about the user and the assessment that is queried.
 * @author EL-Sharkawy
 * @author Kunold
 *
 */
public class ExerciseReviewerProtocol extends AbstractReviewerProtocol {
    

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
        assessments = new ArrayList<>();
    }
    
    /**
     * Returns the currently reviewed assignment.
     * @return The currently reviewed assignment or <tt>null</tt> if {@link #loadAssessments(Assignment)} was not
     *     called before.
     */
    public Assignment getReviewedAssignment() {
        return assignment;
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
        
        getProtocol().getAssessments(assignment.getID(), null).stream()
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
        return super.submitAssessment(assignment, assessment);
    }
    
    /**
     * Retrieves / creates an assessment for the group / user with the specified name.
     * It will create a new (empty) assessment, if there does not exist an assessment for the specified submitter.
     * <b style="color:red">Note:</b> Newly created assignments won't be uploaded to the server automatically.
     * Changes and new assessments must be uploaded via the {@link #submitAssessment(Assessment)} method.
     * @param name The name of the submitter (group name for group submissions, user account name (RZ name) for single
     *     user submissions).
     * @return An {@link Assessment} which may be used to review a submission
     * @throws NetworkException when network problems occur.
     * @see #submitAssessment(Assessment)
     */
    public Assessment getAssessmentForSubmission(String name) throws NetworkException {
        Assessment assessment = getAssessments().stream()
            .filter(a -> name.equals(a.getSubmitterName()))
            .findAny()
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
     * @throws NetworkException when network problems occur.
     */
    private Assessment createAssessment(String name) throws NetworkException {
        Assessment assessment = super.createAssessment(assignment, name);
        assessments.add(assessment);
        return assessment;
    }
    
    
    /**
     * Returns all participating students of the course.
     * Will also store the current group name for each user if an {@link Assignment} is currently reviewed.
     * @return All participants of the course.
     * @throws NetworkException when network problems occur.
     */
    public List<User> loadParticipants() throws NetworkException {
        List<User> participants = new ArrayList<>();
        Map<String, String> groupParticipations = loadGroupNames();
        
        getProtocol().getUsersOfCourse(RoleEnum.STUDENT).stream()
            .map(u -> new User(u.getDisplayName(), u.getUsername(), u.getEmail()))
            .forEach(u -> {
                u.setGroupName(groupParticipations.get(u.getFullName()));
                participants.add(u);
            });
        return participants;
    }

    /**
     * Returns a map that stores for each {@link User} its current group name or <tt>null</tt> in case of a single
     * user assignment.
     * @return A potential empty map in form of (username / full name, group name).
     * @throws NetworkException If network problems occur.
     */
    private Map<String, String> loadGroupNames() throws NetworkException {
        Map<String, String> groupParticipations = new HashMap<>();
        if (null != assignment && assignment.isGroupWork()) {
            List<GroupDto> groups = getProtocol().getGroupsAtAssignmentEnd(assignment.getID());
            for (GroupDto group : groups) {
                group.getMembers().stream()
                    .forEach(p -> groupParticipations.put(p.getUsername(), group.getName()));
            }
        }
        return groupParticipations;
    }
    
    @Override
    public SubmissionTarget getPathToSubmission(Assignment assignment, String submissionName) {
        // Make this method visible as part of the reviewer protocol
        return super.getPathToSubmission(assignment, submissionName);
    }
}
