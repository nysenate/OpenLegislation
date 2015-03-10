package gov.nysenate.openleg.service.auth;

import gov.nysenate.openleg.model.auth.ApiUser;

public interface ApiUserService
{
    public ApiUser registerNewUser(String email, String name, String orgName);

    public ApiUser getUser (String email);

    public void activateUser(String regToken);

    public boolean validateKey(String key);
}
