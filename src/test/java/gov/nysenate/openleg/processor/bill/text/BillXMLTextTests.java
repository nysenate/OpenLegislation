package gov.nysenate.openleg.processor.bill.text;

import gov.nysenate.openleg.dao.bill.data.BillDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.processor.BaseXmlProcessorTest;
import gov.nysenate.openleg.processor.bill.BillXMLBillTextProcessor;
import gov.nysenate.openleg.processor.bill.anact.AnActSobiProcessor;
import gov.nysenate.openleg.processor.sobi.SobiProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * Created by senateuser on 2017/2/28.
 */
public class BillXMLTextTests extends BaseXmlProcessorTest {

    @Autowired
    BillDao billDao;
    @Autowired
    BillXMLBillTextProcessor billXMLBillTextProcessor;
    @Override

    protected SobiProcessor getSobiProcessor() {
        return billXMLBillTextProcessor;
    }

    @Test
    public void replaceSenateBill(){
        final String path = "processor/bill/text/2017-02-09-12.41.27.159140_BILLTEXT_S04325.XML";
        processXmlFile(path);
        Bill b = billDao.getBill(new BillId("S04325", 2017));
        String expectedS = "replace senate bill";
        String actualClause = b.getAmendment(Version.DEFAULT).getFullText();
        assertEquals(expectedS, actualClause);
    }
    @Test
    public void replaceAssemblyBill(){
        final String path = "processor/bill/text/2017-02-09-14.26.07.392403_BILLTEXT_A05464.XML";
        processXmlFile(path);
        Bill b = billDao.getBill(new BillId("A05464", 2017));
        String expectedS = "replace assembly bill";
        String actualClause = b.getAmendment(Version.DEFAULT).getFullText();
        assertEquals(expectedS, actualClause);
    }  @Test
    public void replaceUniBill(){
        final String path = "processor/bill/text/2017-02-01-11.40.11.918032_BILLTEXT_S03526A.XML";
        processXmlFile(path);
        //senate
        Bill b1 = billDao.getBill(new BillId("S03526", 2017));
        String expectedS1 = "replace uni bill";
        String actualClause1 = b1.getAmendment(Version.A).getFullText();
        assertEquals(expectedS1, actualClause1);
        //assembly
        Bill b2 = billDao.getBill(new BillId("A03028", 2017));
        String expectedS2 = "replace uni bill";
        String actualClause2 = b2.getAmendment(Version.A).getFullText();
        assertEquals(expectedS2, actualClause2);
    }  @Test
    public void removeSenateBill(){
        final String path = "processor/bill/text/2017-01-31-10.28.19.879620_BILLTEXT_S03954.XML";
        processXmlFile(path);
        Bill b = billDao.getBill(new BillId("S03954", 2017));
        String expectedS = "";
        String actualClause = b.getAmendment(Version.DEFAULT).getFullText();
        assertEquals(expectedS, actualClause);
    }
    @Test
    public void removeAssemblyBill(){
        final String path = "processor/bill/text/2017-01-31-10.28.49.909483_BILLTEXT_A04029.XML";
        processXmlFile(path);
        Bill b = billDao.getBill(new BillId("A04029", 2017));
        String expectedS = "";
        String actualClause = b.getAmendment(Version.DEFAULT).getFullText();
        assertEquals(expectedS, actualClause);
    }  @Test
    public void removeUniBill(){
        //TODO
    }
    }
