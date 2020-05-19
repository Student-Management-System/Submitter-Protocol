package net.ssehub.exercisesubmitter.protocol.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ssehub.exercisesubmitter.protocol.backend.DataNotFoundException.DataType;
import net.ssehub.studentmgmt.backend_api.ApiClient;
import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssignmentsApi;
import net.ssehub.studentmgmt.backend_api.api.CoursesApi;
import net.ssehub.studentmgmt.backend_api.api.UsersApi;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.StateEnum;
import net.ssehub.studentmgmt.backend_api.model.CourseDto;
import net.ssehub.studentmgmt.backend_api.model.GroupDto;

/**
 * Manages the network protocol communication with the API for the exercise submitter.
 * 
 * @author Kunold
 *
 */
public class NetworkProtocol {
    
    /**
     * The ApiClient enables to set a BasePath for the other API`s.
     */
    private ApiClient apiClient;
    
    /**
     * The API of the user informations.
     */
    private UsersApi apiUser;
    
    /**
     * The API to get the course informations.
     */
    private CoursesApi apiCourse;
    
    
    /**
     * The API to get the assignments informations.
     */
    private AssignmentsApi apiAssignments;
    
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
        apiUser = new UsersApi(apiClient);
        apiCourse = new CoursesApi(apiClient);
        apiAssignments = new AssignmentsApi(apiClient);
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
     */
    NetworkProtocol(String basePath, String courseName, UsersApi apiUser, CoursesApi apiCourse,
        AssignmentsApi apiAssignments) {
        
        this.apiUser = apiUser;
        this.apiCourse = apiCourse;
        this.apiAssignments = apiAssignments;
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
     * Used to select the semester.
     * @param semester The semester to use (four lower case letters + 2 digits).
     */
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    /**
     * Getter for the ID of the course.
     * @return the id of the course.
     * @throws NetworkException when network problems occur.
     */
    public String getCourseID() throws NetworkException {
        if (courseId == null || courseId.isEmpty()) {
            
            try {
                CourseDto course = apiCourse.getCourseByNameAndSemester(courseName, semester);
                courseId = course.getId();
            } catch (IllegalArgumentException e) {
                throw new ServerNotFoundException(e.getMessage(), basePath);
            } catch (ApiException e) {
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
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), basePath);
        } catch (ApiException e) {
            throw new DataNotFoundException("User not found", userID, DataType.USER_NOT_FOUND);
        }
        
        if (null == courses) {
            courses = new ArrayList<>();
        }
        
        return courses;
    }
    
    /**
     * Getter for the groups of a user.
     * @param userID The id of the user whose groups are requested.
     * @return the group of a user.
     * @throws NetworkException when network problems occur.
     */
    public List<GroupDto> getGroups(String userID) throws NetworkException {
        List<GroupDto> groups = null;
        try {
            groups = apiUser.getGroupsOfUserForCourse(userID, getCourseID());
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), basePath);
        } catch (ApiException e) {
            throw new DataNotFoundException("Group not found", userID, DataType.GROUP_NOT_FOUND);
        }
        
        return groups;
    }
    
    /**
     * Getter for all assignments of a course.
     * @return the assignments of a course (will never be <tt>null</tt>).
     * @throws NetworkException when network problems occur.
     */
    public List<AssignmentDto> getAssignments() throws NetworkException {
        List<AssignmentDto> assignments = null;
        try {
            assignments = apiAssignments.getAssignmentsOfCourse(getCourseID());
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), basePath);
        } catch (ApiException e) {
            throw new DataNotFoundException("Assignment not found", getCourseID(), DataType.ASSIGNMENTS_NOT_FOUND);
        }
        
        if (null == assignments) {
            assignments = new ArrayList<>();
        }
        
        return assignments;
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
        } catch (IllegalArgumentException e) {
            throw new ServerNotFoundException(e.getMessage(), basePath);
        } catch (ApiException e) {
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
            List<GroupDto> groups = apiUser.getGroupsOfUserForCourse(userID, getCourseID());
            groupName = groups.get(0).getName();
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NetworkException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return groupName;
    }
    
    /**
     * Returns a map of all specified submissions and their permissions.<br>
     * This is a 2-tuple in the form of <tt>(assignment name, permission)</tt>.
     * The <tt>assignment name</tt> is also used as top level folder inside the repostory to store all submissions
     * related to the assignment.
     * @return A map of all <tt>(assignment name, permission)</tt>s, won't be <tt>null</tt>.
     */
    public Map<String, StateEnum> readPermissions() {
        Map<String, StateEnum> assignments = new HashMap<>();
        
        try {
            for (AssignmentDto dto : getAssignments()) {
                assignments.put(dto.getName(), dto.getState());
            }
        } catch (NetworkException e) {
            // TODO Auto-generated catch block 
            e.printStackTrace();
        }
        
        return assignments;
    }

}
