package gov.nysenate.openleg.client.response.error;

import gov.nysenate.openleg.client.response.base.BaseResponse;

public class ErrorResponse extends BaseResponse {

    protected ErrorCode errorCode;

    public ErrorResponse(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
        this.responseType = "error";
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    public int getErrorCode() {
        return errorCode.getCode();
    }
}
