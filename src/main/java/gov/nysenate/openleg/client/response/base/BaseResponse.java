package gov.nysenate.openleg.client.response.base;

public abstract class BaseResponse
{
    protected boolean success = false;
    protected String message = "";
    protected String responseType = "default";

    public boolean isSuccess() {
        return success;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
