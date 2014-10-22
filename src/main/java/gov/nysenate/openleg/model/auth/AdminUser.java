package gov.nysenate.openleg.model.auth;

import org.mindrot.jbcrypt.BCrypt;

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


    /** Constructor */
   public AdminUser(String name, String pass, int priv) {
        this.username = name;
        this.password = pass;
        this.privilegeLevel = priv;
        encryptPass();
    }

    /**
     * Encrypt passwords before they are stored in the database.
     * This method uses the BCrypt library.
     */
    public void encryptPass() {
        this.password = BCrypt.hashpw(this.password, BCrypt.gensalt());
    }


    /** Getters and Setters */
    public int getPrivileges() { return this.privilegeLevel; }

    public String getUsername() { return this.username; }

    public String getPassword() { return this.password; }

}
