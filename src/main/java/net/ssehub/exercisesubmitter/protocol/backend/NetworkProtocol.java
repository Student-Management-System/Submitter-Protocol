package net.ssehub.exercisesubmitter.protocol.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.exercisesubmitter.protocol.frontend.Assignment;
import net.ssehub.studentmgmt.backend_api.ApiClient;
import net.ssehub.studentmgmt.backend_api.api.AssignmentApi;
import net.ssehub.studentmgmt.backend_api.api.AssignmentRegistrationApi;
import net.ssehub.studentmgmt.backend_api.api.CourseApi;
import net.ssehub.studentmgmt.backend_api.api.GroupApi;
import net.ssehub.studentmgmt.backend_api.api.UserApi;
import net.ssehub.studentmgmt.backend_api.auth.Authentication;
import net.ssehub.studentmgmt.backend_api.auth.OAuth;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.CourseDto;
import net.ssehub.studentmgmt.backend_api.model.GroupDto;

/**
 * Manages the network protocol communication with the API for the exercise submitter.
 * 
 * @author Kunold
 * @author El-Sharkawy
 *
 */
//checkstyle: stop exception type check: Multiple exceptions handles by ApiExceptionHandler
public class NetworkProtocol {
    private static final Logger LOGGER = LogManager.getLogger(NetworkProtocol.class); 
    
    /**
     * The ApiClient enables to set a BasePath for the other API`s.
     */
    private ApiClient apiClient;
    
    /**
     * The API of the user informations.
     */
    private UserApi apiUser;
    
    /**
     * The API to get the course informations.
     */
    private CourseApi apiCourse;
    
    
    /**
     * The API to get the assignments informations.
     */
    private AssignmentApi apiAssignments;
    
    /**
     * Provides information about group/assignment relations.
     */
    private AssignmentRegistrationApi apiAssignmentRegistrations;
    
    /**
     * The API to get the group informations.
     */
    private GroupApi apiGroups;
    
    
    /**
     * The name of the course that uses the exercise submitter.
     * Will be read from the config File.
     */
    private String courseName;
    
    /**
     * Semester should start with 4 lower case letters and do not contain a dash or whitespace.
     */
    private String semester;
    
    /**
     * The URL to the student management REST server.
     */
    private String basePath;
    
    /**
     * The ID of a course.
     */
    private String courseId;
    
    /**
     * The default constructor of the class to be used by the submitters / reviewer.
     * @param basePath The REST URL of the student management server.
     * @param courseName The course that is associated with the exercise submitter.
     */
    public NetworkProtocol(String basePath, String courseName) {
        apiClient = new ApiClient();
        apiClient.setBasePath(basePath);
        apiUser = new UserApi(apiClient);
        apiCourse = new CourseApi(apiClient);
        apiAssignments = new AssignmentApi(apiClient);
        apiAssignmentRegistrations = new AssignmentRegistrationApi(apiClient);
        apiGroups = new GroupApi(apiClient);
        semester = SemesterUtils.getSemester();
        this.courseName = courseName;
        this.basePath = basePath;
    }
    
    /**
     * Constructor intended for testing (Inversion of Control allows setting of Mocks).
     * @param basePath The REST URL of the student management server.
     * @param courseName The course that is associated with the exercise submitter.
     * @param apiUser The API to query <b>user</b> related information.
     * @param apiCourse The API to query <b>course</b> related information.
     * @param apiAssignments The API to query <b>assignment</b> related information.
     * @param apiGroups The API to query <b>group</b> related informations.
     */
    //checkstyle: stop parameter number check
    NetworkProtocol(String basePath, String courseName, UserApi apiUser, CourseApi apiCourse,
        AssignmentApi apiAssignments, GroupApi apiGroups) {
    //checkstyle: start parameter number check  
        this.apiUser = apiUser;
        this.apiCourse = apiCourse;
        this.apiAssignments = apiAssignments;
        this.apiGroups = apiGroups;
        this.courseName = courseName;
        this.basePath = basePath;
        
        // API client not needed during tests
        apiClient = null;
    }
    
    /**
     * Sets the access token after the user has successfully logged in / out.
     * @param accessToken The access token retrieved from the student management system, <tt>null</tt> to log out.
     */
    public void setAccessToken(String accessToken) {
        apiClient.setAccessToken(accessToken);
    }
    
    /**
     * <font color="red"><b>Designed for testing only.</b></font>
     * Returns the currently used access token to the <b>student management service</b>.
     * Reversed engineered method, not very stable and, thus, should not be used in productive code.
     * @return The access token or <tt>null</tt> if not set.
     */
    public String getAccessToken() {
        OAuth usedAuth = null;
        for (Authentication auth : apiClient.getAuthentications().values()) {
            if (auth instanceof OAuth) {
                usedAuth = (OAuth) auth;
            }
        }
        
        return null != usedAuth ? usedAuth.getAccessToken() : null;
    }
    
    /**
     * The URL to the student management REST server.
     * @return The URL to the student management REST server.
     */
    protected String getBasePath() {
        return basePath;
    }
    
    /**
     * The name of the course that uses the exercise submitter.
     * @return the The name of the course that uses the exercise submitter.
     */
    protected String getCourseName() {
        return courseName;
    }
    
    /**
     * Allows subclasses to use the {@link ApiClient}, which serves as connection to the REST-server.
     * @return The used {@link ApiClient}.
     */
    protected ApiClient getApiClient() {
        return apiClient;
    }
    
    /**
     * Returns the API to query for groups.
     * @return The API to query for groups.
     */
    protected GroupApi getGroupsApi() {
        return apiGroups;
    }
    
    /**
     * Returns the API to query for users.
     * @return The API to query for users.
     */
    protected UserApi getUsersApi() {
        return apiUser;
    }
    
    /**
     * Used to select the semester.
     * @param semester The semester to use (four lower case letters + 2 digits).
     */
    public void setSemester(String semester) {
        this.semester = semester;
        courseId = null;
    }
    
    /**
     * Getter for the ID of the course.
     * @return the id of the course.
     * @throws NetworkException when network problems occur.
     */
    public String getCourseID() throws NetworkException {
        if (courseId == null || courseId.isEmpty()) {
            
            try {
                CourseDto course = apiCourse.getCourses(null, null, courseName, semester, null).stream()
                    .filter(c -> c.getShortname().equals(courseName))
                    .findAny()
                    .orElseThrow(() -> new DataNotFoundException("Course not found", courseName,
                        DataType.COURSE_NOT_FOUND));
                courseId = course.getId();
            } catch (Exception e) {
                ApiExceptionHandler.handleException(e, getBasePath());
                throw new DataNotFoundException("Course not found", courseName, DataType.COURSE_NOT_FOUND);
            }
        }
                
        return courseId;
    }
    
    /**
     * Getter for the Courses of a user.
     * @param userID The id of the user whose course is requested.
     * @return A List of all courses from the user (will never be <tt>null</tt>).
     * @throws NetworkException when network problems occur.
     */
    public List<CourseDto> getCourses(String userID) throws NetworkException {
        List<CourseDto> courses = null;
        try {
            courses = apiUser.getCoursesOfUser(userID);
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("User not found", userID, DataType.USER_NOT_FOUND);
        }
        
        if (null == courses) {
            courses = new ArrayList<>();
        }
        
        return courses;
    }
    
    /**
     * Getter for all assignments of a course.
     * @param states Optional filters for {@link AssignmentDto} that matches the specified states, will return all
     *     assignments if states are <tt>null</tt> or empty.
     * @return the assignments of a course (will never be <tt>null</tt>).
     * @throws NetworkException when network problems occur.
     */
    public List<Assignment> getAssignments(AssignmentDto.StateEnum... states) throws NetworkException {
        final List<Assignment> assignments = new ArrayList<>();
        try {
            apiAssignments.getAssignmentsOfCourse(getCourseID()).stream()
                // Arrays as stream based on https://stackoverflow.com/a/1128728
                .filter(a -> null == states || states.length == 0
                    || Arrays.stream(states).anyMatch(a.getState()::equals))
                .map(a -> toAssignment(a))
                .filter(a -> null != a)
                .forEach(assignments::add);
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("Assignment not found", getCourseID(), DataType.ASSIGNMENTS_NOT_FOUND);
        }
        
        return assignments;
    }
    
    /**
     * Part of {@link #getAssignments(net.ssehub.studentmgmt.backend_api.model.AssignmentDto.StateEnum) to handle
     * exception in cases of data, which cannot be handled by the tools.
     * @param dto The {@link AssignmentDto} retrieved by the REST server.
     * @return The equivalence to be handled by the reviewer / submitters
     */
    private static Assignment toAssignment(AssignmentDto dto) {
        Assignment assignment = null;
        try {
            assignment = new Assignment(dto);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Error occured while handling {}, discared it because of {}", dto, e.getMessage());
        }
        return assignment;
    }
    
    /**
     * Getter for the Assessments of a user.
     * @param userId The user whose assessments are requested.
     * @return The Assessments from a user (will never be <tt>null</tt>).
     * @throws NetworkException when network problems occur.
     */
    public List<AssessmentDto> getAssessmentsWithGroups(String userId) throws NetworkException {
        List<AssessmentDto> assessments = null;
        try {
            assessments = apiUser.getAssessmentsOfUserForCourse(userId, getCourseID());
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("Assessments not found", userId, DataType.ASSESSMENTS_NOT_FOUND);
        }
        
        if (null == assessments) {
            assessments = new ArrayList<>();
        }
        
        return assessments;
    }
    
    /**
     * Returns the destination path of an <b>Assessment</b> for a <b>Student</b> inside the repository, considering
     * its <b>Group</b> assignment for that specific assessment.
     * Will retrieve the correct group assignment automatically.
     * @param userID The user whose assessments are requested.
     * @param assignmentID The ID of the the assessment that is requested.
     * @return A list of folders to traverse form the repository root to the upload destination.
     * @throws NetworkException If network problems occur.
     */
    public String getGroupForAssignment(String userID, String assignmentID) throws NetworkException {
        String groupName = null;
        try {
            GroupDto groupDto = apiUser.getGroupOfAssignment(userID, getCourseID(), assignmentID);
            groupName = groupDto.getName();
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("No assignment related group information found", assignmentID,
               DataType.GROUP_NOT_FOUND);
        }
        
        return groupName;
    }
    
    /**
     * Getter for the groups of an assignment at submission end.
     * @param assignmentId The ID of the assignment for that the groups are requested.
     * @return A list of groups at the end of submission. List will be empty if no results are found.
     * @throws NetworkException If network problems occur.
     */
    public List<GroupDto> getGroupsAtAssignmentEnd(String assignmentId) throws NetworkException {
        List<GroupDto> groups = null;
        
        try {
            groups = apiAssignmentRegistrations.getRegisteredGroups(getCourseID(), assignmentId, null, null, null);
        } catch (Exception e) {
            ApiExceptionHandler.handleException(e, getBasePath());
            throw new DataNotFoundException("No Groups for the assignment found", assignmentId, 
                    DataType.GROUP_NOT_FOUND);
        }
        
        if (null == groups) {
            groups = new ArrayList<>();
        }
        
        return groups;
    }
}
// checkstyle: start exception type check
