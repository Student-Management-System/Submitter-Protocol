package net.ssehub.exercisesubmitter.protocol.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.studentmgmt.backend_api.api.AssessmentsApi;
import net.ssehub.studentmgmt.backend_api.api.AssignmentsApi;
import net.ssehub.studentmgmt.backend_api.api.CourseParticipantsApi;
import net.ssehub.studentmgmt.backend_api.api.CoursesApi;
import net.ssehub.studentmgmt.backend_api.api.GroupsApi;
import net.ssehub.studentmgmt.backend_api.api.UsersApi;
import net.ssehub.studentmgmt.backend_api.model.AssessmentCreateDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentUpdateDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.ParticipantDto;
import net.ssehub.studentmgmt.backend_api.model.ParticipantDto.RoleEnum;
import net.ssehub.studentmgmt.backend_api.model.UserDto;

/**
 * Network protocol designed for the &quot;Exercise Reviewer&quot;.
 * @author El-Sharkawy
 * @author Kunold
 *
 */
//checkstyle: stop exception type check: Multiple exceptions handles by ApiExceptionHandler
public class ReviewerProtocol extends NetworkProtocol {
    
    /**
     * The API to get the assessment informations.
     */
    private AssessmentsApi apiAssessments;
    
    /**
     * The API to get information about participants of a course.
     */
    private CourseParticipantsApi apiParticipants;
    
    /**
     * The default constructor of the class to be used by the reviewer.
     * @param basePath The REST URL of the student management server.
     * @param courseName The course that is associated with the ExerciseReviewer.
     */
    public ReviewerProtocol(String basePath, String courseName) {
        super(basePath, courseName);
        // Use always getApiClient() to keep same settings (e.g., setting of access token)
        apiAssessments = new AssessmentsApi(getApiClient());
        apiParticipants = new CourseParticipantsApi(getApiClient());
    }
    
    /**
     * Constructor intended for testing (Inversion of Control allows setting of Mocks).
     * @param basePath The REST URL of the student management server.
     * @param courseName The course that is associated with the exercise submitter.
     * @param apiUser The API to query <b>user</b> related information.
     * @param apiCourse The API to query <b>course</b> related information.
     * @param apiAssignments The API to query <b>assignment</b> related information.
     * @param apiAssessments The API to query <b>assessment</b> related information.
     * @param apiGroup The API to query <b>group</b> related information.
     */
    //checkstyle: stop parameter number check
    ReviewerProtocol(String basePath, String courseName, UsersApi apiUser, CoursesApi apiCourse,
            AssignmentsApi apiAssignments, AssessmentsApi apiAssessments, GroupsApi apiGroup) {
    //checkstyle: start parameter number check
        super(basePath, courseName, apiUser, apiCourse, apiAssignments, apiGroup);
        this.apiAssessments = apiAssessments;
    }
    
    /**
     * Getter for the Assessments of an Assignment.
     * @param assignmentId the id of the specified assignment.
     * @param groupName Optional: The name of the group (in case of group submission).
     *     If this is <tt>null</tt>, all assessments are returned.<br/>
     *     <b style="color:red">Warning:</b> Must not be used if assignment is a single user assignment!
     * @return List of Assessments.
     * @throws NetworkException when network problems occur.
     */
    public List<AssessmentDto> getAssessments(String assignmentId, String groupName) throws NetworkException {
        List<AssessmentDto> assessments = null;
        try {
            assessments = apiAssessments.getAssessmentsForAssignment(super.getCourseID(), assignmentId, null, null,
                groupName, null, null, null, null);
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("Assessments not found", getCourseName(), DataType.ASSESSMENTS_NOT_FOUND);
        }
        
        return assessments;
    }
    
    /**
     * Returns the list of all participants (users, tutors, lecturers) of a course.
     * @param courseRoles Optional list of roles to filter.
     * @return All participants of the current course or only users that have one of the specified roles.
     * @throws NetworkException when network problems occur.
     */
    public List<ParticipantDto> getUsersOfCourse(RoleEnum... courseRoles) throws NetworkException {
        List<ParticipantDto> users = null;
        try {
            List<String> roles = null;
            if (null != courseRoles) {
                roles = new ArrayList<>();
                for (RoleEnum role : courseRoles) {
                    roles.add(role.name());
                }
            }
            users = apiParticipants.getUsersOfCourse(getCourseID(), null, null, roles, null);            
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("User(s) not found", getCourseName(), DataType.USER_NOT_FOUND);
        }
        
        return users;
    }
    
    /**
     * Getter for one Assessment of an Assignment.
     * @param assignmentId the id of the specified assignment.
     * @param assessmentId the id of the specified assessment.
     * @return the assessment of an assignment.
     * @throws NetworkException when network problems occur.
     */
    public AssessmentDto getAssessmentForAssignment(String assignmentId, String assessmentId) 
        throws NetworkException {
        
        AssessmentDto singleAssessment = null;
        try {
            singleAssessment = apiAssessments.getAssessmentById(super.getCourseID(), assignmentId, assessmentId);
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("Assessments for the specified assignment not found", getCourseName(),
                DataType.ASSESSMENTS_NOT_FOUND);
        }
        
        return singleAssessment;
    }
    
    /**
     * Getter for one Assessment of an Assignment.
     * @param assignmentId the id of the specified assignment.
     * @param assessmentId the id of the specified assessment.
     * @return the assessment of an assignment.
     * @throws NetworkException when network problems occur.
     */
    public boolean assessmentExists(String assignmentId, String assessmentId) throws NetworkException {
        boolean exists = false;
        try {
            apiAssessments.getAssessmentById(super.getCourseID(), assignmentId, assessmentId);
            exists = true;
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            // No action needed -> exists = false
        }
        
        return exists;
    }
    
    /**
     * Creates the Assessment for an Assignment.
     * @param body The Assessment body.
     * @param assignmentId The id of the specified assignment.
     * @return The ID on the server of the newly created assessment.
     * @throws NetworkException when network problems occur.
     */
    public String createAssessment(AssessmentCreateDto body, String assignmentId) throws NetworkException {
        String id;
        try {
            AssessmentDto result = apiAssessments.createAssessment(body, super.getCourseID(), assignmentId);
            id = result.getId();
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("Assessmentbody not found", getCourseName(),
                DataType.ASSESSMENT_BODY_NOT_FOUND);
        }
        
        return id;
    }
    
    /**
     * Creates a Partial Assessment.
     * @param body The body of the partial assessment
     * @param assignmentId The id of the specified assignment.
     * @param assessmentId The id of the specified assessment.
     * @return True if PartialAssessment was created successfully, False otherwise.
     * @throws NetworkException when network problems occur.
     */
    public boolean createPartialAssessment(PartialAssessmentDto body, String assignmentId, String assessmentId) 
            throws NetworkException {
        boolean success = false;
        try {
            PartialAssessmentDto result = apiAssessments.addPartialAssessment(body, super.getCourseID(), assignmentId,
                assessmentId);
            success = result != null;
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("Assessmentbody not found", getCourseName(),
                DataType.ASSESSMENT_BODY_NOT_FOUND);
        }
        
        return success;
    }
    
    /**
     * Updates an Assessment.
     * @param body The body of the assessment that is updated.
     * @param assignmentId The id of the specified assignment.
     * @param assessmentId The id of the specified assessment.
     * @return True if Assessment was updated successfully, False otherwise.
     * @throws NetworkException when network problems occur.
     */
    public boolean updateAssessment(AssessmentUpdateDto body, String assignmentId, String assessmentId) 
        throws NetworkException {

        boolean success = false;
        try {
            AssessmentDto result = apiAssessments.updateAssessment(body, super.getCourseID(), assignmentId, 
                assessmentId);
            success = result != null;
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("Assessmentbody not found", getCourseName(),
                DataType.ASSESSMENT_BODY_NOT_FOUND);
        }
        return success;
    }
    
    /**
     * Deletes an Assessment.
     * @param assignmentId The id of the specified assignment.
     * @param assessmentId The id of the specified assessment.
     * @return True if Assessment was deleted successfully, False otherwise.
     * @throws NetworkException when network problems occur.
     */
    public boolean deleteAssessment(String assignmentId, String assessmentId) throws NetworkException {
        boolean success = false;
        try {
            apiAssessments.deleteAssessment(super.getCourseID(), assignmentId, assessmentId);
            // API definition: This method was successful iff no exception was thrown
            success = true;
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("Assessment not found", getCourseName(), DataType.ASSESSMENTS_NOT_FOUND);
        }
        
        return success;
    }
    
    /**
     * Returns the user by the specified ID.
     * @param userID An ID created by the student management system.
     * @return The specified user.
     * @throws NetworkException when network problems occur.
     */
    public UserDto getUserById(String userID) throws NetworkException {
        UserDto user = null;
        try {
            user = getUsersApi().getUserById(userID);
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("User not found", userID, DataType.USER_NOT_FOUND);
        }
        
        return user;
    }
    
    /**
     * Returns a user that is a <tt>student</tt> by the specified name.
     * @param userName The expected user name (RZ name) of the user to search for.
     * @return The specified user or <tt>null</tt> if it could not be found.
     * @throws NetworkException when network problems occur.
     */
    public ParticipantDto getStudentByName(String userName) throws NetworkException {
        ParticipantDto participant = null;
        try {
            List<String> roles = Arrays.asList(ParticipantDto.RoleEnum.STUDENT.getValue());
            participant = apiParticipants.getUsersOfCourse(getCourseID(), null, null, roles, null).stream()
                .filter(p -> p.getUsername().equals(userName))
                .findAny()
                .orElse(null);
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("User not found", userName, DataType.USER_NOT_FOUND);
        }
        
        return participant;
    }
}
//checkstyle: start exception type check