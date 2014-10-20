package gov.nysenate.openleg.model.auth;

/**
 * Represents the possible roles for an admin account,
 * Read Only, Write Only, Both permissions
 */

public class AdminRole
{
    /** The admin's Role */
    private RoleType roleType;

    /** Constructor */
     public AdminRole(RoleType r) {
        this.roleType = r;
    }

    /** Create a new admin. role from a String containing the specified role */

    public static AdminRole createRole(String role) {
        if (role.equalsIgnoreCase("Read"))
            return new AdminRole(RoleType.READ);
        else if (role.equalsIgnoreCase("Write"))
            return new AdminRole(RoleType.WRITE);
        else if (role.equalsIgnoreCase("ReadWrite"))
            return new AdminRole(RoleType.READ_WRITE);
        else
            return null;
    }

    /** An enum containing the different possible role types for admin accounts. */
    public enum RoleType {
        READ,
        WRITE,
        READ_WRITE
    }

    /** Get role type */
    public RoleType getRoleType()
    {
        return this.roleType;
    }


    @Override
    public String toString() {
        switch (this.roleType)
        {
            case READ:
                return "ReadOnly";
            case WRITE:
                return "WriteOnly";
            case READ_WRITE:
                return "ReadWrite";
        }
        return "";
    }

}
