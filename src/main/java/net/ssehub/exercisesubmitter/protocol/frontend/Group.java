package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Manages the Groups and Members.
 * 
 * @author El-Sharkawy
 * @author Kunold
 * @author Adam
 */
public class Group implements Iterable<User>, Comparable<Group> {
    
    private String groupName;
    
    private Set<User> members = new TreeSet<>();
    
    /**
     * Creates a group.
     * 
     * @param groupName The name of this group.
     */
    public Group(String groupName) {
        this.groupName = groupName;
    }
    
    /**
     * Creates a {@link Group} for a single {@link Individual} (student). This may be used for non-group assignments.
     * 
     * @param student A single student.
     * 
     * @return A {@link Group} with a single {@link Individual} and the name of that {@link Individual} as the group
     *      name.
     */
    public static Group createSingleStudentGroup(User student) {
        Group singleStudent = new Group(student.getAccountName());
        singleStudent.addMembers(student);
        return singleStudent;
    }
    
    /**
     * Getter for the Name of the Group.
     * 
     * @return the group name.
     */
    public String getName() {
        return groupName;
    }
    
    /**
     * Adds Members to a group.
     * 
     * @param members that belongs to a group.
     */
    public void addMembers(User... members) {
        if (null != members) {
            for (int i = 0; i < members.length; i++) {
                this.members.add(members[i]);
            }
        }
    }
    
    /**
     * Returns the full list of group members.
     * @return The members of the group.
     */
    public Set<User> getMembers() {
        return members;
    }

    @Override
    public Iterator<User> iterator() {
        return members.iterator();
    }
    
    @Override
    public int compareTo(Group other) {
        return this.groupName.compareTo(other.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, members);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Group)) {
            return false;
        }
        Group other = (Group) obj;
        return Objects.equals(groupName, other.groupName) && Objects.equals(members, other.members);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Group [groupName=");
        builder.append(groupName);
        builder.append(", members=");
        builder.append(members);
        builder.append("]");
        return builder.toString();
    }
    
}
