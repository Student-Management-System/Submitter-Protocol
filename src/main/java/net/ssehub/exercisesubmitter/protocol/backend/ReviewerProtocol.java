package net.ssehub.exercisesubmitter.protocol.backend;

import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssessmentsApi;
import net.ssehub.studentmgmt.backend_api.api.AssignmentsApi;
import net.ssehub.studentmgmt.backend_api.api.CoursesApi;
import net.ssehub.studentmgmt.backend_api.api.UsersApi;
import net.ssehub.studentmgmt.backend_api.model.AssessmentCreateDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;

/**
 * Network protocol designed for the &quot;Exercise Reviewer&quot;.
 * @author El-Sharkawy
 * @author Kunold
 *
 */
public class ReviewerProtocol extends NetworkProtocol {
    
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
     */
    //checkstyle: stop parameter number check
    ReviewerProtocol(String basePath, String courseName, UsersApi apiUser, CoursesApi apiCourse,
            AssignmentsApi apiAssignments, AssessmentsApi apiAssessments) {
    //checkstyle: start parameter number check
        super(basePath, courseName, apiUser, apiCourse, apiAssignments);
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
    public boolean updateAssessment(AssessmentDto body, String assignmentId, String assessmentId) 
            throws NetworkException {
        boolean success = false;
        try {
            AssessmentDto result =  apiAssessments.updateAssessment(body, super.getCourseID(), assignmentId, 
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
}
