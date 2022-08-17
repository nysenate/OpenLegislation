package gov.nysenate.openleg.api.response;

import gov.nysenate.openleg.api.ViewObject;

public class ViewObjectResponse<ViewType extends ViewObject> extends BaseResponse {
    private final ViewType result;

    public ViewObjectResponse(ViewType result) {
        this(result, "");
    }

    public ViewObjectResponse(ViewType result, String message) {
        this.result = result;
        if (result != null) {
            success = true;
            responseType = result.getViewType();
        }
        this.message = message;
    }

    public ViewType getResult() {
        return result;
    }
}
