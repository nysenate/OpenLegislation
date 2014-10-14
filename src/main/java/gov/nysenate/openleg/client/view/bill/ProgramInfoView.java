package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.ProgramInfo;

public class ProgramInfoView implements ViewObject {

    protected String name;
    protected int num;

    public ProgramInfoView(ProgramInfo programInfo) {
        if (programInfo != null) {
            this.name = programInfo.getInfo();
            this.num = programInfo.getNumber();
        }
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    @Override
    public String getViewType() {
        return "program-info";
    }
}
