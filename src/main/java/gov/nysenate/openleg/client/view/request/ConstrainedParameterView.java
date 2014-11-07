package gov.nysenate.openleg.client.view.request;

public class ConstrainedParameterView extends ParameterView {

    protected String constraint;

    public ConstrainedParameterView(String name, String type, String constraint) {
        super(name, type);
        this.constraint = constraint;
    }

    @Override
    public String getViewType() {
        return "parameter-constrained";
    }

    public String getConstraint() {
        return constraint;
    }
}
