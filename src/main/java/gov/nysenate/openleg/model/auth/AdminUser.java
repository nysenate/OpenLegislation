package gov.nysenate.openleg.model.auth;

/**
 * This class will model an administrator.
 */

public class AdminUser
{
    /** For Testing: 0 = Read Only, 1 = Write Only, 2 = Read and Write */
    private int privilegeLevel;

    /** The Admin's username */
    private String username;

    /** The admin's password */
    private String password;

    /** Whether or not the admin is active */
    private boolean active;


    /** Constructor */
   public AdminUser(String name, String pass, int priv) {
        this.username = name;
        this.password = pass;
        this.active = false;
        this.privilegeLevel = priv;
    }

    /** Getters and Setters */
    public int getPrivileges() { return this.privilegeLevel; }
    public void setPrivilegeLevel(int newLevel) { this.privilegeLevel = newLevel; }

    public String getUsername() { return this.username; }
    public void setUsername(String newName) { this.username = newName; }

    public String getPassword() { return this.password; }

    public void setPassword(String newPass) {
        this.password = newPass;
    }

    public boolean isActive() { return this.active; }
    public void setActive(boolean b) {
        this.active = b;
    }
}
