package gov.nysenate.openleg.service.bill.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillInfo;
import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.util.List;
import java.util.Optional;

/**
 * Created by kyle on 3/5/15.
 */
public class APIBillDataService implements BillDataService {
    @Override
    public Bill getBill(BaseBillId billId) throws BillNotFoundEx {
        return null;
    }

    @Override
    public BillInfo getBillInfo(BaseBillId billId) throws BillNotFoundEx {
        return null;
    }

    @Override
    public List<BaseBillId> getBillIds(SessionYear sessionYear, LimitOffset limitOffset) {
        return null;
    }

    @Override
    public int getBillCount(SessionYear sessionYear) {
        return 0;
    }

    @Override
    public void saveBill(Bill bill, SobiFragment fragment, boolean postUpdateEvent) {

    }

    @Override
    public Optional<Range<SessionYear>> activeSessionRange() {
        return null;
    }
}
