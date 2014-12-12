package gov.nysenate.openleg.client.view.error;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.request.ConstrainedParameterView;
import gov.nysenate.openleg.controller.api.base.InvalidRequestParamEx;

public class InvalidParameterView implements ViewObject {

    protected ConstrainedParameterView parameterConstraint;
    protected String receivedValue;

    public InvalidParameterView(InvalidRequestParamEx ex) {
        this.parameterConstraint = new ConstrainedParameterView(ex.getParameterName(), ex.getParameterType(),
                ex.getParameterConstraint());
        this.receivedValue = ex.getParameterValue();
    }

    @Override
    public String getViewType() {
        return "invalid-parameter";
    }

    public ConstrainedParameterView getParameterConstraint() {
        return parameterConstraint;
    }

    public String getReceivedValue() {
        return receivedValue;
    }
}
