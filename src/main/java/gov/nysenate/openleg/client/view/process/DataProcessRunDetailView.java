package gov.nysenate.openleg.client.view.process;

import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.model.process.DataProcessRun;
import gov.nysenate.openleg.model.process.DataProcessUnit;

import static java.util.stream.Collectors.toList;

public class DataProcessRunDetailView extends DataProcessRunView implements ViewObject
{
    protected ListViewResponse<DataProcessUnitView> details;

    /** --- Constructors --- */

    public DataProcessRunDetailView(DataProcessRun run, PaginatedList<DataProcessUnit> units) {
        super(run);
        if (units != null) {
            this.details = ListViewResponse.of(
                units.getResults().stream().map(DataProcessUnitView::new).collect(toList()),
                units.getTotal(), units.getLimOff());
        }
    }

    @Override
    public String getViewType() {
        return "data-process-run-detail";
    }

    /** --- Basic Getters --- */

    public ListViewResponse<DataProcessUnitView> getDetails() {
        return details;
    }
}
