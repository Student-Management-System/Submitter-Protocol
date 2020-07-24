package net.ssehub.exercisesubmitter.protocol.frontend;

/**
 * Represents (group) participants that belong to one submission.
 * @author El-Sharkawy
 *
 */
public class User {

    private String fullName;
    private String accountName;
    
    /**
     * Creates a new user instance, that represents a user of a submissions, which should be reviewed as part of an
     * {@link Assessment}.
     * @param fullName The full name (first + last name) of the user.
     * @param accountName The account name (RZ name) of the user.
     */
    protected User(String fullName, String accountName) {
        this.fullName = fullName;
        this.accountName = accountName;
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

    @Override
    public int hashCode() {
        // Generated
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountName == null) ? 0 : accountName.hashCode());
        result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        // Generated
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        if (accountName == null) {
            if (other.accountName != null) {
                return false;
            }
        } else if (!accountName.equals(other.accountName)) {
            return false;
        }
        if (fullName == null) {
            if (other.fullName != null) {
                return false;
            }
        } else if (!fullName.equals(other.fullName)) {
            return false;
        }
        return true;
    }
    
}
