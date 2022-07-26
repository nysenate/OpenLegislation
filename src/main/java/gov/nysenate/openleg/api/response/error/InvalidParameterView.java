package gov.nysenate.openleg.api.response.error;

import gov.nysenate.openleg.api.InvalidRequestParamEx;
import gov.nysenate.openleg.api.ViewObject;

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
}
