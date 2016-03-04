package gov.nysenate.openleg.client.view.process;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.process.DataProcessRunInfo;

public class DataProcessRunInfoView implements ViewObject
{
    protected DataProcessRunView run;
    protected DataProcessUnitView first;
    protected DataProcessUnitView last;

    public DataProcessRunInfoView(DataProcessRunInfo runInfo) {
        this.run = new DataProcessRunView(runInfo.getRun());
        if (runInfo.getFirstProcessed().isPresent()) {
            this.first = new DataProcessUnitView(runInfo.getFirstProcessed().get());
        }
        if (runInfo.getLastProcessed().isPresent()) {
            this.last = new DataProcessUnitView(runInfo.getLastProcessed().get());
        }
    }

    @Override
    public String getViewType() {
        return "data-process-run-info";
    }

    /** --- Basic Getters --- */

    public DataProcessRunView getRun() {
        return run;
    }

    public DataProcessUnitView getFirst() {
        return first;
    }

    public DataProcessUnitView getLast() {
        return last;
    }
}
