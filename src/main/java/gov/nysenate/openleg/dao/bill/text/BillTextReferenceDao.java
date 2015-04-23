package gov.nysenate.openleg.dao.bill.text;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextSpotcheckReference;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by kyle on 2/19/15.
 */
public interface BillTextReferenceDao {

    public BillTextSpotcheckReference getMostRecentBillTextReference(BaseBillId id, LocalDateTime start, LocalDateTime end);
    public BillTextSpotcheckReference getMostRecentBillTextReference(LocalDateTime start, LocalDateTime end);
    public BillTextSpotcheckReference getPKBillTextReference(BaseBillId id, LocalDateTime refDateTime);
    public void addBillToScrapeQueue(BaseBillId baseBillId);
    public List<BaseBillId> getScrapeQueue();
    public List<BillTextSpotcheckReference> getBillTextReference(BaseBillId id);
    public BillTextSpotcheckReference getBillTextReference(BaseBillId id, LocalDateTime refDateTime);
    public void insertBillTextReference(BillTextSpotcheckReference ref);
    public void deleteBillTextReference(BillTextSpotcheckReference ref);
    public void deleteBillFromScrapeQueue(BaseBillId id);

    }
