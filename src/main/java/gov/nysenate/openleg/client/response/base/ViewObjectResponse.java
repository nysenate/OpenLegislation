package gov.nysenate.openleg.client.response.base;

import gov.nysenate.openleg.client.view.base.ViewObject;

public class ViewObjectResponse<ViewType extends ViewObject> extends BaseResponse
{
    private ViewType result;

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
