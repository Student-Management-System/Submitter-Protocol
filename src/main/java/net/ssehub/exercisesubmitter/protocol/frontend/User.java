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
        return Objects.hash(accountName, eMail, fullName);
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
                && Objects.equals(fullName, other.fullName);
    }

    @Override
    public String toString() {
        return getFullName() + " <" + getEMail() + ">";
    }
    
}
