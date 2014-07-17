package gov.nysenate.openleg.processors.sobi.bill;

import gov.nysenate.openleg.model.bill.BillId;

import java.util.Set;

public class BillProcessorOptions
{
    private Set<BillId> restrictToSet;

    public Set<BillId> getRestrictToSet() {
        return restrictToSet;
    }

    public void setRestrictToSet(Set<BillId> restrictToSet) {
        this.restrictToSet = restrictToSet;
    }
}
