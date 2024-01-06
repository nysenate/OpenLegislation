package gov.nysenate.openleg.api.legislation.agenda;

import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;

public record AgendaBillInfo(BillId billId, BillInfo billInfo, String message) {}
