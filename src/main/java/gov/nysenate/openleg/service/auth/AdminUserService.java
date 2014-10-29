package gov.nysenate.openleg.service.auth;

public interface AdminUserService
{
    public int login (String username, String password) throws AuthenticationEx;

    public void createUser (String username, String password);

    public void changePass (String username, String passNew);
}
