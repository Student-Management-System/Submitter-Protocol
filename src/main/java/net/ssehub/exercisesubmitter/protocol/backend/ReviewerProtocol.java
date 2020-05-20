package net.ssehub.exercisesubmitter.protocol.backend;

import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssessmentsApi;
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
     * @throws NetworkException when network problems occur.
     */
    public void createAssessment(AssessmentCreateDto body, String assignmentId) throws NetworkException {
        try {
            apiAssessments.createAssessment(body, super.getCourseID(), assignmentId);
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            throw new DataNotFoundException("Assessmentbody not found", getCourseName(),
                DataType.ASSESSMENT_BODY_NOT_FOUND);
        }
    }
    
    /**
     * Creates a Partial Assessment.
     * @param body The body of the partial assessment
     * @param assignmentId The id of the specified assignment.
     * @param assessmentId The id of the specified assessment.
     * @throws NetworkException when network problems occur.
     */
    public void createPartialAssessment(PartialAssessmentDto body, String assignmentId, String assessmentId) 
            throws NetworkException {
        try {
            apiAssessments.addPartialAssessment(body, super.getCourseID(), assignmentId, assessmentId);
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            throw new DataNotFoundException("Assessmentbody not found", getCourseName(),
                DataType.ASSESSMENT_BODY_NOT_FOUND);
        }
    }
    
    /**
     * Updates an Assessment.
     * @param body The body of the assessment that is updated.
     * @param assignmentId The id of the specified assignment.
     * @param assessmentId The id of the specified assessment.
     * @throws NetworkException when network problems occur.
     */
    public void updateAssessment(AssessmentDto body, String assignmentId, String assessmentId) throws NetworkException {
        try {
            apiAssessments.updateAssessment(body, super.getCourseID(), assignmentId, assessmentId);
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            throw new DataNotFoundException("Assessmentbody not found", getCourseName(),
                DataType.ASSESSMENT_BODY_NOT_FOUND);
        }
    }
    
    /**
     * Deletes an Assessment.
     * @param assignmentId The id of the specified assignment.
     * @param assessmentId The id of the specified assessment.
     * @throws NetworkException when network problems occur.
     */
    public void deleteAssessment(String assignmentId, String assessmentId) throws NetworkException {
        try {
            apiAssessments.deleteAssessment(super.getCourseID(), assignmentId, assessmentId);
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), getBasePath());
        } catch (ApiException e) {
            throw new DataNotFoundException("Assessment not found", getCourseName(), DataType.ASSESSMENTS_NOT_FOUND);
        }
    }
}
