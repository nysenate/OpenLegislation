package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.dao.bill.text.SqlFsBillTextReferenceDao;
import gov.nysenate.openleg.dao.spotcheck.BaseBillIdSpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

/**
 * Created by kyle on 3/12/15.
 */
@Service("LRSBillTextReport")
public class BillTextReportService extends BaseSpotCheckReportService {

    @Autowired private SqlFsBillTextReferenceDao dao;
    @Autowired private BillDataService billDataService;
    @Autowired private BaseBillIdSpotCheckReportDao reportDao;
    @Autowired private BillTextCheckService billTextCheckService;

    @Override
    protected SpotCheckReportDao getReportDao() {
        return reportDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_SCRAPED_BILL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpotCheckReport<BaseBillId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        List<BillTextReference> references = dao.getUncheckedBillTextReferences();
        if (references.isEmpty()) {
            throw new ReferenceDataNotFoundEx();
        }
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();
        report.setReportId(new SpotCheckReportId(SpotCheckRefType.LBDC_SCRAPED_BILL,
                references.get(0).getReferenceDate(), LocalDateTime.now()));

        // Set checked billids as notes
        report.setNotes(references.stream()
                .map(btr -> btr.getBaseBillId().toString())
                .reduce("", (a, b) -> a + (StringUtils.isBlank(a) ? "" : ", ") + b));

        // Get observations for each reference
        references.stream()
                .map(this::generateObservation)
                .forEach(report::addObservation);

        // Set each reference as checked
        references.stream()
                .map(BillTextReference::getBaseBillId)
                .forEach(dao::setChecked);

        return report;
    }

    /* --- Internal Methods --- */

    private SpotCheckObservation<BaseBillId> generateObservation(BillTextReference btr) {
        //Gets bill from openleg processed info
        try {
            Bill bill = billDataService.getBill(new BaseBillId(btr.getPrintNo(), btr.getSessionYear()));
            return billTextCheckService.check(bill, btr);
        } catch (BillNotFoundEx e) {
            SpotCheckObservation<BaseBillId> ob = new SpotCheckObservation<>(btr.getReferenceId(), btr.getBaseBillId());
            if (btr.isNotFound()) { // Bill text references are still generated if LRS data is not found
                ob.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING,
                        "also missing", btr.getBaseBillId() + "\n" + btr.getStrippedText()));
            }
            ob.addMismatch(new SpotCheckMismatch(OBSERVE_DATA_MISSING, "", btr.getBaseBillId().toString()));
            return ob;
        }
    }
}
