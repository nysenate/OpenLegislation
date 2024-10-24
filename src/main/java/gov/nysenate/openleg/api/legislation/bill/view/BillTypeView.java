package gov.nysenate.openleg.api.legislation.bill.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.bill.BillType;

public class BillTypeView implements ViewObject
{
    protected String chamber;
    protected String desc;
    protected boolean resolution;

    public BillTypeView(BillType billType) {
        if (billType != null) {
            this.chamber = billType.getChamber().name();
            this.desc = billType.getName();
            this.resolution = billType.isResolution();
        }
    }

    protected BillTypeView(){
        super();
    }

    public String getChamber() {
        return chamber;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isResolution() {
        return resolution;
    }

    @Override
    public String getViewType() {
        return "bill-type";
    }
}