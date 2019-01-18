package gov.nysenate.openleg;

import gov.nysenate.openleg.annotation.SillyTest;
import gov.nysenate.openleg.client.view.bill.BillPdfView;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillTextFormat;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.EnumSet;

@Category(SillyTest.class)
public class SillyTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

    /** --- Your silly tests go here --- */

    /*
                      __     __,
                      \,`~"~` /
      .-=-.           /    . .\
     / .-. \          {  =    Y}=
    (_/   \ \          \      /
           \ \        _/`'`'`b
            \ `.__.-'`        \-._
             |            '.__ `'-;_
             |            _.' `'-.__)
              \    ;_..--'/     //  \
              |   /  /   |     //    |
              \  \ \__)   \   //    /
               \__)        './/   .'
                             `'-'`
    */

    @Autowired BillDataService bds;

    @Test
    public void sillyTest() throws Exception {
//        List<BaseBillId> billIds = bds.getBillIds(SessionYear.of(2017), LimitOffset.ALL);
//        Collections.shuffle(billIds);
//        for (BaseBillId id : billIds.subList(0, 100)) {
//            Bill bill = bds.getBill(id, EnumSet.allOf(BillTextFormat.class));
//            File f = new File("/tmp/pdfs/" + id.toString() + ".pdf");
//            BillPdfView.writeBillPdf(bill, bill.getActiveVersion(), new FileOutputStream(f));
//        }
        File f = new File("/tmp/J174.pdf");
        BillPdfView.writeBillPdf(bds.getBill(new BaseBillId("J174", 2017), EnumSet.allOf(BillTextFormat.class)), Version.ORIGINAL, new FileOutputStream(f));
    }
}
