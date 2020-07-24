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
}
