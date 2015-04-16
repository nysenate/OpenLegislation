package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckObservation;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextSpotcheckReference;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

/**
 * Created by kyle on 2/19/15.
 */
@Service
public class BillTextCheckService implements SpotCheckService<BaseBillId, Bill, BillTextSpotcheckReference>{
    private static final Logger logger = Logger.getLogger(BillTextCheckService.class);

    @Autowired
    BillDataService billDataService;

    @PostConstruct
    public void init(){

    }

    /*public BillTextCheckService() throws Exception{
        BillTextScraper billTextScraper = new BillTextScraper();
        List<File> textList = billTextScraper.scrape();
        // look at other objects that implement BillDataService to see how they use the getBill method
        //BillDataService billDataService = new APIBillDataService();
        //Bill b = billDataService.getBill(new BaseBillId(printNo, Integer.parseInt(sessionYear)));
        //ObservationView<BaseBillId> check = new ObservationView<BaseBillId>(new BaseBillId(printNo, Integer.parseInt(sessionYear)));
        SpotCheckObservation<BaseBillId> check = new SpotCheckObservation<>();
    }*/

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill content) throws ReferenceDataNotFoundEx {

        return null;
    }

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill content, LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        return null;
    }

    @Override
    public SpotCheckObservation<BaseBillId> check(Bill content, BillTextSpotcheckReference reference) {

        return new SpotCheckObservation<BaseBillId>(new SpotCheckReferenceId(SpotCheckRefType.LBCD_SCRAPED_BILL,
                content.getPublishedDateTime()), content.getBaseBillId());  // x = new SpotCheckObservation<BaseBillId>();

    }
}