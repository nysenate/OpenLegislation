package gov.nysenate.openleg.service.auth;

public class AuthenticationEx extends RuntimeException
{
       public AuthenticationEx(String username, String pass) {
            super();
           System.err.printf("Invalid user/pass combo with user %s and pass %s", username, pass);
       }
}
