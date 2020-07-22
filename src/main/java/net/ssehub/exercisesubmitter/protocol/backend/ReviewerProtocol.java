package net.ssehub.exercisesubmitter.protocol.backend;

import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssessmentsApi;
import net.ssehub.studentmgmt.backend_api.api.AssignmentsApi;
import net.ssehub.studentmgmt.backend_api.api.CoursesApi;
import net.ssehub.studentmgmt.backend_api.api.GroupsApi;
import net.ssehub.studentmgmt.backend_api.api.UsersApi;
import net.ssehub.studentmgmt.backend_api.model.AssessmentCreateDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentUpdateDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.UserDto;

/**
 * Network protocol designed for the &quot;Exercise Reviewer&quot;.
 * @author El-Sharkawy
 * @author Kunold
 *
 */
public class ReviewerProtocol extends NetworkProtocol {
    private static final String USER = "user";
    private static final String MAX_POINTS = "*max*";
    private static final String SEPARATOR = "\t";
    private static final String LINE_BREAK = System.lineSeparator();
    
    /**
     * The API to get the assessment informations.
     */
    private AssessmentsApi apiAssessments;
    
    /**
     * The default constructor of the class to be used by the reviewer.
     * @param basePath The REST URL of the student management server.
     * @param courseName The course that is associated with the ExerciseReviewer.
     */
    public ReviewerProtocol(String basePath, String courseName) {
        super(basePath, courseName);
        apiAssessments = new AssessmentsApi(getApiClient());
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
     * @return List of Assessments.
     * @throws NetworkException when network problems occur.
     */
    public List<AssessmentDto> getAssessments(String assignmentId) throws NetworkException {
        List<AssessmentDto> assessments = null;
        try {
            assessments = apiAssessments.getAllAssessmentsForAssignment(super.getCourseID(), assignmentId);
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            if (401 == e.getCode()) {
                throw new UnauthorizedException("User not authorized, but required to query Assessments.");
            }
            throw new DataNotFoundException("Assessments not found", getCourseName(), DataType.ASSESSMENTS_NOT_FOUND);
        }
        
        return assessments;
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
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            throw new DataNotFoundException("Assessments for the specified assignement not found", getCourseName(),
                    DataType.ASSESSMENTS_NOT_FOUND);
        }
        return singleAssessment;
    }
    
    /**
     * Creates the Assessment for an Assignment.
     * @param body The Assessment body.
     * @param assignmentId The id of the specified assignment.
     * @return True if Assessment was created successfully, False otherwise.
     * @throws NetworkException when network problems occur.
     */
    public boolean createAssessment(AssessmentCreateDto body, String assignmentId) throws NetworkException {
        boolean success = false;
        try {
            AssessmentDto result = apiAssessments.createAssessment(body, super.getCourseID(), assignmentId);
            success = result != null;
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            throw new DataNotFoundException("Assessmentbody not found", getCourseName(),
                DataType.ASSESSMENT_BODY_NOT_FOUND);
        }
        return success;
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
            PartialAssessmentDto result =  apiAssessments.addPartialAssessment(body, super.getCourseID(), assignmentId,
                    assessmentId);
            success = result != null;
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
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
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
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
            success = apiAssessments.deleteAssessment(super.getCourseID(), assignmentId, assessmentId);
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            throw new DataNotFoundException("Assessment not found", getCourseName(), DataType.ASSESSMENTS_NOT_FOUND);
        }
        return success;
    }
    
    /**
     * Returns a formated String with all users and their points to an assignment.
     * @param assignmentId The ID of the assignment.
     * @return All users and their points to an assignment.
     */
    public String getSubmissionRealUsersReviews(String assignmentId) throws NetworkException {
        String userReviews = "";
        List<AssessmentDto> assessments = null;
        
        try {
            assessments = apiAssessments.getAllAssessmentsForAssignment(super.getCourseID(), assignmentId);
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            throw new DataNotFoundException("Assessments not found", getCourseName(), DataType.ASSESSMENTS_NOT_FOUND);
        }
        
        boolean firstIteration = true;
        for (AssessmentDto assessment : assessments) {
            // is only added once to the string at the top of the string
            if (firstIteration) {
                //first line: user  taskname    taskname    taskname
                userReviews = USER + SEPARATOR + assessment.getAssignment().getName() + SEPARATOR 
                        + assessment.getAssignment().getName() + LINE_BREAK;
                //second line: *max*    points  points  points
                userReviews += MAX_POINTS + SEPARATOR + assessment.getAssignment().getPoints() + SEPARATOR 
                        + assessment.getAssignment().getPoints() + LINE_BREAK;
                
                firstIteration = false;
            }
            
            // vollername   punkte  bewertung   upload erfolgreich(momentan nicht abrufbar)
            for (UserDto user : assessment.getGroup().getUsers()) {
                userReviews += user.getUsername() + SEPARATOR + assessment.getAchievedPoints() + SEPARATOR 
                        + assessment.getComment() + LINE_BREAK;
            }
        }
        
        return userReviews;
    }
    
    /**
     * Returns a formated String with all groups and their users.
     * @param assignmentId The ID of the assignment.
     * @return All users whose submission is reviewed.
     */
    public String getSubmissionReviewerUsers(String assignmentId) throws NetworkException {
        String submissionUsers = "";
        List<AssessmentDto> assessments = null;
        
        try {
            assessments = apiAssessments.getAllAssessmentsForAssignment(super.getCourseID(), assignmentId);
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            throw new DataNotFoundException("Assessments not found", getCourseName(), DataType.ASSESSMENTS_NOT_FOUND);
        }
        
        //gruppenname   vollername    rz-kennung  uni-mail
        for (AssessmentDto assessment : assessments) {
            for (UserDto user : assessment.getGroup().getUsers()) {
                submissionUsers += assessment.getGroup().getName() + SEPARATOR + user.getUsername() + SEPARATOR 
                        + user.getRzName() + SEPARATOR + user.getEmail() + LINE_BREAK;
            }
        }
        
        return submissionUsers;
    }
    
    
    /**
     * Returns a formated String with all groups and there review.
     * @param assignmentId The ID of the assignment.
     * @return All groups whose submission is reviewed.
     * @throws NetworkException when network problems occur.
     */
    public String getSubmissionReviews(String assignmentId) throws NetworkException {
        String submissionReviews = "";
        List<AssessmentDto> assessments = null;
        
        try {
            assessments = apiAssessments.getAllAssessmentsForAssignment(super.getCourseID(), assignmentId);
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            throw new DataNotFoundException("Assessments not found", getCourseName(), DataType.ASSESSMENTS_NOT_FOUND);
        }
        
        boolean firstIteration = true;
        // counter is used to know when the last line is written, so there won´t be a line break
        int counter = assessments.size();
        for (AssessmentDto assessment : assessments) {
            // is only added once to the string at the top of the string
            if (firstIteration) {
                //first line: user  taskname    taskname    taskname
                submissionReviews = USER + SEPARATOR + assessment.getAssignment().getName() + SEPARATOR 
                        + assessment.getAssignment().getName() + LINE_BREAK;
                //second line: *max*    points  points  points
                submissionReviews += MAX_POINTS + SEPARATOR + assessment.getAssignment().getPoints() + SEPARATOR 
                        + assessment.getAssignment().getPoints() + LINE_BREAK;
                
                firstIteration = false;
            }
            
            //gruppenname   punkte  kommentar   upload erfolgreich
            if (counter > 1) {
                submissionReviews += assessment.getGroup().getName() + SEPARATOR + assessment.getAchievedPoints() 
                    + SEPARATOR + assessment.getComment() + LINE_BREAK;                
            } else {
                submissionReviews += assessment.getGroup().getName() + SEPARATOR + assessment.getAchievedPoints() 
                    + SEPARATOR + assessment.getComment();
            }
            counter--;
        }
        
        return submissionReviews;
    }
}
