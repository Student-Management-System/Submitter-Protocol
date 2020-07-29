package net.ssehub.exercisesubmitter.protocol.frontend;


import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;

/**
 * Stores information about assignments (definition of homework / exams).
 * This class wraps the information of the {@link AssignmentDto} to <b>reduce the impact</b>
 * of changes of the REST server.
 * 
 * @author El-Sharkawy
 *
 */
public class Assignment {
    
    /**
     * State whether the assignment is open to be edited and submitted or in review.
     * @author El-Sharkawy
     *
     */
    public static enum State {
        INVISIBLE,
        SUBMISSION,
        IN_REVIEW,
        REVIEWED;
    }
    private static final Logger LOGGER = LogManager.getLogger(Assignment.class);

    private String name;
    private String assignmentID;
    private State state;
    private boolean isGroupwork;
    private double points;
    
    /**
     * Instantiates a new assignments for exercises based on an {@link AssignmentDto} retrieved from the server.
     * @param dto The data received from the server.
     * @throws IllegalArgumentException If data was received which cannot be handled by the exercise
     *     submitters / reviewer.
     */
    public Assignment(AssignmentDto dto) throws IllegalArgumentException {
        this.name = dto.getName();
        this.assignmentID = dto.getId();
        this.points = dto.getPoints().doubleValue();
        
        if (null == dto.getState()) {
            String errMsg = name + " has no state";
            LOGGER.warn(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        
        if (null == dto.getCollaboration()) {
            String errMsg = "No collaboration type defined for " + name;
            LOGGER.warn(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        
        switch (dto.getState()) {
        case INVISIBLE:
            state = State.INVISIBLE;
            break;
        case IN_PROGRESS:
            state = State.SUBMISSION;
            break;
        case IN_REVIEW:
            state = State.IN_REVIEW;
            break;
        case CLOSED:
            // TODO SE: Check if this is a already evaluated work or a work which was not open to students.
            // falls through
        case EVALUATED:
            state = State.REVIEWED;
            break;
        default:
            state = State.INVISIBLE;
            LOGGER.warn("Unsupported assingment state retrieved {}, handled as {}", dto.getState(), state);
            break;            
        }
        
        switch (dto.getCollaboration()) {
        case GROUP:
            isGroupwork = true;
            break;
        case SINGLE:
            isGroupwork = false;
            break;
        case GROUP_OR_SINGLE:
            // falls through
        default:
            String errMsg = "Unsupported collaboration type retrieved " + dto.getCollaboration().name();
            LOGGER.warn(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
    }
    
    /**
     * Creates manually an Assignment.
     * @param name The name of the assignment
     * @param assignmentID The ID used by the REST system, may be <tt>null</tt> during unit tests
     * @param state The state of the assignment. 
     * @param isGroupwork <tt>true</tt> for groups, <tt>false</tt> for individuals.
     */
    public Assignment(String name, String assignmentID, State state, boolean isGroupwork) {
        this.name = name;
        this.assignmentID = assignmentID;
        this.state = state;
        this.isGroupwork = isGroupwork;
    }
    
    /**
     * Returns the name of the assignment.
     * @return The name of the assignment.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the ID of the assignment to query the REST server, should not be used by the submitter/reviewer directly.
     * @return The ID to query the REST server for additional information.
     */
    protected String getID() {
        return assignmentID;
    }
    
    /**
     * The state of the assignment.
     * @return The state of the assignment.
     */
    public State getState() {
        return state;
    }
    
    /**
     * Returns whether the assignment should be done in home work groups are by individuals.
     * @return <tt>true</tt> for groups, <tt>false</tt> for individuals.
     */
    public boolean isGroupWork() {
        return isGroupwork;
    }
    
    /**
     * The points that can be achieved at the assignment.
     * @return The points of the assignment.
     */
    public double getPoints( ) {
        return points;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(assignmentID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Assignment)) {
            return false;
        }
        Assignment other = (Assignment) obj;
        return Objects.equals(assignmentID, other.assignmentID);
    }

    @Override
    public String toString() {
        // Only used for Debugging purpose
        return getName();
    }
}
