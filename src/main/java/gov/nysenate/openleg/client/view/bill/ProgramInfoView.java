package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.ProgramInfo;

public class ProgramInfoView implements ViewObject {

    protected String info;
    protected int infoNum;

    public ProgramInfoView(ProgramInfo programInfo) {
        if (programInfo != null) {
            this.info = programInfo.getInfo();
            this.infoNum = programInfo.getNumber();
        }
    }

    public String getInfo() {
        return info;
    }

    public int getInfoNum() {
        return infoNum;
    }

    @Override
    public String getViewType() {
        return "program-info";
    }
}
