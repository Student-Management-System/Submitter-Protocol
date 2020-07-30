package net.ssehub.exercisesubmitter.protocol.frontend;

import java.util.Objects;

/**
 * Represents (group) participants that belong to one submission.
 * @author El-Sharkawy
 *
 */
public class User {

    private String fullName;
    private String accountName;
    private String eMail;
    private String groupName;
    
    /**
     * Creates a new user instance, that represents a user of a submissions, which should be reviewed as part of an
     * {@link Assessment}.
     * @param fullName The full name (first + last name) of the user.
     * @param accountName The account name (RZ name) of the user.
     * @param eMail The e-mail address of the user.
     */
    protected User(String fullName, String accountName, String eMail) {
        this.fullName = fullName;
        this.accountName = accountName;
        this.eMail = eMail;
    }
    
    /**
     * Sets a group name.
     * @param groupName The name of the group where the user is currently attending, or <tt>null</tt> in case of single
     * user assignments.
     */
    protected void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    /**
     * Returns the group name of the student. <br>
     * <b style="color:red">Note:</b> The group name is dependent of the currently reviewed assignment and may be <tt>
     * null</tt> in case of a single user assignment. This should only be called for information purpose and <b>not</b>
     * to compute submission destinations. 
     * @return The group name of the user at the currently reviewed assignment (this may become wrong if an former
     *     user object is used after the assignment was changed at the protocol).
     */
    public String getGroupName() {
        return groupName;
    }
    
    /**
     * Returns the full name (first + last name) of the user.
     * @return The full name
     */
    public String getFullName() {
        return fullName;
    }
    
    /**
     * Returns the account name (RZ name) of the user.
     * @return The account name.
     */
    public String getAccountName() {
        return accountName;
    }
    
    /**
     * Returns the e-mail address of the user.
     * @return The e-mail address of the user.
     */
    public String getEMail() {
        return eMail;
    }

    /**
     * <b>Generated.</b><p>
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(accountName, eMail, fullName, groupName);
    }

    /**
     * <b>Generated.</b><p>
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        return Objects.equals(accountName, other.accountName) && Objects.equals(eMail, other.eMail)
                && Objects.equals(fullName, other.fullName) && Objects.equals(groupName, other.groupName);
    }

    @Override
    public String toString() {
        return getFullName() + " <" + getEMail() + ">";
    }
    
}
