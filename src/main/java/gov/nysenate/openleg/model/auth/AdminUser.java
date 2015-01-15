package gov.nysenate.openleg.model.auth;

/**
 * This class will model an administrator.
 */

public class AdminUser
{
    /** The Admin's username */
    private String username;

    /** The admin's password */
    private String password;

    /** Whether or not the admin is active */
    private boolean active;

    /** Designates the user's status as a master admin */
    private boolean master;

    /** Constructor */
    public AdminUser(String name, String pass, boolean active, boolean master) {
        this.username = name;
        this.password = pass;
        this.active = active;
        this.master = master;
    }

    /** Getters and Setters */

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

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }
}
