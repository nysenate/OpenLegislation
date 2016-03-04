package gov.nysenate.openleg.client.view.error;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.request.ConstrainedParameterView;
import gov.nysenate.openleg.controller.api.base.InvalidRequestParamEx;

public class InvalidParameterView implements ViewObject {

    protected ConstrainedParameterView parameterConstraint;
    protected String receivedValue;

    public InvalidParameterView(InvalidRequestParamEx ex) {
        this(ex.getParameterName(), ex.getParameterType(), ex.getParameterConstraint(), ex.getParameterValue());
    }

    public InvalidParameterView(String name, String type, String constraint, String receivedValue) {
        this.parameterConstraint = new ConstrainedParameterView(name, type, constraint);
        this.receivedValue = receivedValue;
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
