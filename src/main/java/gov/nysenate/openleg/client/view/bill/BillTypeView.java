package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BillType;

public class BillTypeView implements ViewObject
{
    protected String chamber;
    protected String desc;
    protected boolean resolution;
    protected boolean budget;
    protected String specialBudget = "";

    public BillTypeView(BillType billType) {
        if (billType != null) {
            this.chamber = billType.getChamber().name();
            this.desc = billType.getName();
            this.resolution = billType.isResolution();
        }
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

    public boolean isBudget() {
        return budget;
    }

    public String getSpecialBudget() {
        return specialBudget;
    }

    @Override
    public String getViewType() {
        return "bill-type";
    }
}