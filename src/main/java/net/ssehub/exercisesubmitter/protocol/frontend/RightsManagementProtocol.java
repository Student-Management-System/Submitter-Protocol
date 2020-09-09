package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.ArrayList;
import java.util.List;

import net.ssehub.exercisesubmitter.protocol.backend.NetworkException;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto.StateEnum;
import net.ssehub.studentmgmt.backend_api.model.GroupDto;
import net.ssehub.studentmgmt.backend_api.model.ParticipantDto;
import net.ssehub.studentmgmt.backend_api.model.ParticipantDto.RoleEnum;

/**
 * Protocol for the <b>Rights Management</b> service that updates the submission repository according to course changes
 * done at the <b>student management system</b>.
 * @author El-Sharkawy
 *
 */
public class RightsManagementProtocol extends AbstractReviewerProtocol {
    private String courseName;

    /**
     * Creates a new {@link RightsManagementProtocol} instance.
     * @param authenticationURL The URL of the authentication server (aka Sparky service)
     * @param stdMgmtURL The URL of the student management service
     * @param courseName The course that is associated with the exercise submitter.
     * @param submissionServer The root (URL) where to submit assignments (exercises).
     */
    public RightsManagementProtocol(String authenticationURL, String stdMgmtURL, String courseName,
        String submissionServer) {
        super(authenticationURL, stdMgmtURL, courseName, submissionServer);
        this.courseName = courseName;
    }
    
    /**
     * Sets the access token if already logged in.
     * @param accessToken The access token to the student management service to use.
     */
    public void setAccessToken(String accessToken) {
        getProtocol().setAccessToken(accessToken);
    }
    
    /**
     * Pulls the list of tutors from the server.
     * @return A group containing all tutors.
     * @throws NetworkException If network problems occur.
     */
    public Group getTutors() throws NetworkException {
        String tutorsGroupName = "Tutors_of_Course_" + courseName.substring(0, 1).toUpperCase()
            + courseName.substring(1);
        Group tutors = new Group(tutorsGroupName);
        getProtocol().getUsersOfCourse(RoleEnum.LECTURER, RoleEnum.TUTOR).stream()
            .map(p -> super.convertToUser(p))
            .forEach(u -> {
                u.setGroupName(tutorsGroupName);
                tutors.addMembers(u);
            });
        
        return tutors;
    }
    
    /**
     * Pulls the list of participating students from the server.
     * This list is independent of groups.
     * @return The list of students of the course.
     * @throws NetworkException If network problems occur.
     */
    public List<User> getStudents() throws NetworkException {
        List<User> students = new ArrayList<User>();
        getProtocol().getUsersOfCourse(RoleEnum.STUDENT).stream()
            .map(p -> super.convertToUser(p))
            .forEach(students::add);
        
        return students;
    }
    
    /**
     * Pulls the information of configured homework groups from the <b>student management system</b> for an assignment.
     * @param assignment The assignment for which the group shall be loaded from the server.
     * @return The configured homework groups of the <b>student management system</b>.
     * @throws NetworkException 
     */
    public List<Group> loadGroupsPerAssignment(Assignment assignment) throws NetworkException {
        // Gather all homework groups for an assignment
        List<Group> homeworkGroups = new ArrayList<>();
        
        List<GroupDto> groupsOfServer = getProtocol().getGroupsAtAssignmentEnd(assignment.getID());
        for (GroupDto groupDto : groupsOfServer) {
            Group group = new Group(groupDto.getName());
            
            for (ParticipantDto userDto : groupDto.getMembers()) {
                User user = convertToUser(userDto);
                user.setGroupName(groupDto.getName());
                group.addMembers(user);
            }
            homeworkGroups.add(group);
        }
        
        return homeworkGroups;
    }
    
    /**
     * Pulls the information of configured {@link ManagedAssignment}s from the <b>student management system</b>.
     * @param studentsOfCourse A cached list of known participants of the course, used to reduce traffic.
     *     Maybe <tt>null</tt>, in this case the list is pulled from the server.
     * @return The assignments of the course, containing the participants of the assignments (students in case of
     *     single assignments, otherwise the groups).
     */
    public List<ManagedAssignment> loadAssignments(List<User> studentsOfCourse) throws NetworkException {
        List<ManagedAssignment> assignments = new ArrayList<>();
        getProtocol().getAssignments((StateEnum) null).stream()
            .map(a -> new ManagedAssignment(a))
            .forEach(assignments::add);
        
        for (ManagedAssignment assignment : assignments) {
            if (assignment.isGroupWork()) {
                List<Group> homeworkGroups = loadGroupsPerAssignment(assignment);
                assignment.addAllGroups(homeworkGroups);   
            } else {
                if (null == studentsOfCourse) {
                    studentsOfCourse = getStudents();
                }
                studentsOfCourse.stream()
                    .map(user -> Group.createSingleStudentGroup(user))
                    .forEach(group -> assignment.addGroup(group));
            }            
        }
            
        return assignments;
    }

}
