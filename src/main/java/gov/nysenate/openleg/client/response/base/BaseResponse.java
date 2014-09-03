package gov.nysenate.openleg.client.response.base;

public class BaseResponse
{
    protected boolean success = false;
    protected String message = "";

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
