package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.ProgramInfo;

public class ProgramInfoView implements ViewObject {

    protected String name;
    protected int sequenceNo;

    public ProgramInfoView(ProgramInfo programInfo) {
        if (programInfo != null) {
            this.name = programInfo.getInfo();
            this.sequenceNo = programInfo.getNumber();
        }
    }

    public String getName() {
        return name;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    @Override
    public String getViewType() {
        return "program-info";
    }
}
