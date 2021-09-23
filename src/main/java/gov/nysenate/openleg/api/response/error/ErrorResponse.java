package gov.nysenate.openleg.api.response.error;

import gov.nysenate.openleg.api.response.BaseResponse;

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
