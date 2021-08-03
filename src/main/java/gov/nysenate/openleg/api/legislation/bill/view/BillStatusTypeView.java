package gov.nysenate.openleg.api.legislation.bill.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.bill.BillStatusType;

public class BillStatusTypeView implements ViewObject {

    private String name;
    private String description;

    public BillStatusTypeView() {}

    public BillStatusTypeView(BillStatusType type) {
        this.name = type.name();
        this.description = type.getDesc();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getViewType() {
        return "bill-status-type";
    }
}
