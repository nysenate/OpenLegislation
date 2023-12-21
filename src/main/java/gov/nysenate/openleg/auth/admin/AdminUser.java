package gov.nysenate.openleg.auth.admin;

/**
 * This class will model an administrator.
 */
public class AdminUser {
    private String username;
    private String password;
    private boolean active;
    private boolean isMaster;

    public AdminUser(String name, String pass, boolean active, boolean isMaster) {
        this.username = name;
        this.password = pass;
        this.active = active;
        this.isMaster = isMaster;
    }

    /** Getters and Setters */

    public String getUsername() {
        return this.username;
    }
    public void setUsername(String newName) {
        this.username = newName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String newPass) {
        this.password = newPass;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean b) {
        this.active = b;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        this.isMaster = master;
    }
}
