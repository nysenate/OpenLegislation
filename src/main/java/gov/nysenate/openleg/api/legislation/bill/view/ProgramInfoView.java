package gov.nysenate.openleg.api.legislation.bill.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.bill.ProgramInfo;

public class ProgramInfoView implements ViewObject {

    protected String name;
    protected int sequenceNo;

    public ProgramInfoView(ProgramInfo programInfo) {
        if (programInfo != null) {
            this.name = programInfo.getInfo();
            this.sequenceNo = programInfo.getNumber();
        }
    }

    protected ProgramInfoView(){
        super();
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
