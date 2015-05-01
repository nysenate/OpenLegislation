package gov.nysenate.openleg.stupid;

import com.google.common.base.Utf8;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillInfo;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.bill.data.CachedBillDataService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kyle on 10/8/14.
 */
public class KTests{
    @Autowired
    private CachedBillDataService billDataService;

    @Test
    public void test() throws Exception {
        BaseBillId bid = new BaseBillId("S2", SessionYear.of(2015));
        Bill bill;
        bill = billDataService.getBill(bid);
        System.out.println("________________________________");
        //System.out.println("print no:::: "+ bill.getBasePrintNo());
        //System.out.println("Amend:::: "+ bill.getBaseBillId().getVersion());
        System.out.println("Session Year:::: "+ bill.getSession());
        System.out.println("summary:::: "+ bill.getSummary());

        //System.out.println("memo:::: "+ bill.getActiveVersion().getMemo());
        //System.out.println("text:::: "+ bill.getActiveVersion().getFullText());

    }

}