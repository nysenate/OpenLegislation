package gov.nysenate.openleg.service.spotcheck;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;

/**
 * Simple marker interface to represent spot check services that operate on Bills.
 * This is mostly here for convenience when autowiring.
 */
public interface SpotCheckBillService extends SpotCheckService<BaseBillId, Bill> {}