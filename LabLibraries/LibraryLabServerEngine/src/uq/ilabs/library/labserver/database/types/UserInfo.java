/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.labserver.database.types;

import java.util.Calendar;

/**
 *
 * @author uqlpayne
 */
public class UserInfo {

    public static final int MAXLEN_Username = 32;
    public static final int MAXLEN_FirstName = 64;
    public static final int MAXLEN_LastName = 64;
    public static final int MAXLEN_ContactEmail = 128;
    public static final int MAXLEN_UserGroup = 64;
    public static final int MAXLEN_Password = 40;
    //
    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private String contactEmail;
    private String userGroup;
    private String password;
    private boolean accountLocked;
    private Calendar dateCreated;
    private Calendar dateModified;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Calendar getDateModified() {
        return dateModified;
    }

    public void setDateModified(Calendar dateModified) {
        this.dateModified = dateModified;
    }

    public UserInfo() {
        this.userId = -1;
    }

    public UserInfo(String username, String firstName, String lastName, String contactEmail, String userGroup, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactEmail = contactEmail;
        this.userGroup = userGroup;
        this.password = password;
        this.accountLocked = false;
    }
}
