package gov.nysenate.openleg.model.auth;

/**
 * This class will model an administrator.
 */

public class AdminUser
{
    /** The Admin's role */
    private AdminRole role;

    /** The Admin's username */
    private String userName;

    /** The admin's password */
    private String password;


    /** Constructor */
   public AdminUser(String name, String pass, String role) {
        this.userName = name;
        this.password = pass;
        this.role = AdminRole.createRole(role);
        encryptPass();
    }

    /**
     * Encrypt passwords before they are stored in the database.
     * To be completed?
     */
    public void encryptPass() {

    }


    /** Getters and Setters */
    public AdminRole getRole() { return this.role; }

    public String getUserName() { return this.userName; }

    public String getPassword() { return this.password; }

}
