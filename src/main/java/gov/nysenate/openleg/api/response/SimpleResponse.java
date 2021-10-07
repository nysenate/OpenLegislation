package gov.nysenate.openleg.api.response;

public class SimpleResponse extends BaseResponse
{
    public SimpleResponse(boolean success, String message, String type) {
        this.success = success;
        this.message = message;
        this.responseType = type;
    }
}
