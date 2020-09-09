package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;

/**
 * An {@link Assignment} and its registered groups.
 * This is intended for the {@link RightsManagementProtocol}, which configured group submissions
 * for each {@link Assignment}.
 * @author El-Sharkawy
 *
 */
public class ManagedAssignment extends Assignment implements Iterable<Group> {

    private Set<Group> groups = new TreeSet<>();

    /**
     * Instantiates a new assignments for exercises based on an {@link AssignmentDto} retrieved from the server.
     * @param dto The data received from the server.
     * @throws IllegalArgumentException If data was received which cannot be handled by the exercise
     *     submitters / reviewer.
     */
    public ManagedAssignment(AssignmentDto dto) throws IllegalArgumentException {
        super(dto);
    }
    
    /**
     * Creates manually an Assignment.
     * @param name The name of the assignment
     * @param assignmentID The ID used by the REST system, may be <tt>null</tt> during unit tests
     * @param state The state of the assignment. 
     * @param isGroupwork <tt>true</tt> for groups, <tt>false</tt> for individuals.
     * @param points The amount of achievable points.
     */
    public ManagedAssignment(String name, String assignmentID, State state, boolean isGroupwork, double points) {
        super(name, assignmentID, state, isGroupwork, points);
    }
    
    /**
     * Creates a new {@link ManagedAssignment} based on a {@link Assignment}.
     * @param assignment The assignment to copy into this class format. 
     */
    public ManagedAssignment(Assignment assignment) {
        this(assignment.getName(), assignment.getID(), assignment.getState(), assignment.isGroupWork(),
            assignment.getPoints());
    }

    /**
     * Adds a group that works on this assignment.
     * @param group A group working on this assignment.
     */
    public void addGroup(Group group) {
        groups.add(group);
    }
    
    /**
     * Adds all specified groups to the assignment.
     * 
     * @param groups To add to the set.
     */
    public void addAllGroups(Collection<Group> groups) {
        for (Group group : groups) {
            addGroup(group);
        }
    }
    
    /**
     * Overrides the previously stored groups.
     * 
     * @param groups The new list of groups
     */
    public void setGroups(List<Group> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
    }
    
    /**
     * Returns a sorted array of all group names of this exercise.
     * 
     * @return An array of all group names.
     */
    public String[] getAllGroupNames() {
        return groups.stream()
                .map((group) -> group.getName())
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }
    
    @Override
    public Iterator<Group> iterator() {
        return groups.iterator();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(groups);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof ManagedAssignment)) {
            return false;
        }
        ManagedAssignment other = (ManagedAssignment) obj;
        return Objects.equals(groups, other.groups);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Assignment [getName()=");
        builder.append(getName());
        builder.append(", isGroupWork()=");
        builder.append(isGroupWork());
        builder.append(", groups=");
        builder.append(groups);
        builder.append("]");
        return builder.toString();
    }
}
