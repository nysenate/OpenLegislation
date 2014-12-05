package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A BillView with some extra details.
 */
public class DetailBillView extends BillView implements ViewObject
{
    private static final Logger logger = LoggerFactory.getLogger(DetailBillView.class);

    /** Contains BillInfoViews keyed by the BaseBillId string for every other bill that is referenced by this bill.
     *  This map eliminates possible duplications where for example a same as bill reference is identical to the
     *  substituted by reference. */
    protected MapView<String, BillInfoView> billInfoRefs;

    /** --- Constructors --- */

    public DetailBillView(Bill bill, BillDataService billDataService) {
        super(bill);

        Map<String, BillInfoView> refs = new HashMap<>();
        // Previous version refs
        bill.getPreviousVersions().stream().forEach(billId -> addBillInfoRefToMap(billDataService, refs, billId));
        // Same as refs from each amendment
        bill.getAmendmentList().stream().flatMap(a -> a.getSameAs().stream())
            .forEach(billId -> addBillInfoRefToMap(billDataService, refs, billId));
        // Substituted by ref
        addBillInfoRefToMap(billDataService, refs, bill.getSubstitutedBy());

        this.billInfoRefs = MapView.of(refs);
    }

    /** --- Basic Getters/Setters --- */

    @Override
    public String getViewType() {
        return "detailed-bill-view";
    }

    public MapView<String, BillInfoView> getBillInfoRefs() {
        return billInfoRefs;
    }

    /** --- Internal --- */

    /**
     * Checks if the supplied map has a key with the given bill id and associates a BillInfoView if found.
     */
    private void addBillInfoRefToMap(BillDataService billDataService, Map<String, BillInfoView> refs, BillId billId) {
        if (billId != null) {
            BaseBillId baseBillId = BaseBillId.of(billId);
            if (!refs.containsKey(baseBillId.toString())) {
                try {
                    refs.put(baseBillId.toString(), new BillInfoView(billDataService.getBillInfo(baseBillId)));
                }
                catch (BillNotFoundEx ex) {
                    logger.trace("Bill reference not found while constructing detailed bill view", ex);
                }
            }
        }
    }
}