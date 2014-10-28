package gov.nysenate.openleg.controller.api.bill;

import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.search.BillSearchService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for Bill API controllers to inherit,
 */
public abstract class BillBaseCtrl extends BaseCtrl
{
    @Autowired
    protected BillDataService billData;

    @Autowired
    protected BillSearchService billSearch;
}