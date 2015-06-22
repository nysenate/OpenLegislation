package gov.nysenate.openleg.stupid;

import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.client.view.spotcheck.ReportDetailView;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.text.SqlFsBillTextReferenceDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.model.spotcheck.billtext.BillScrapeQueueEntry;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.model.spotcheck.billtext.ScrapeQueuePriority;
import gov.nysenate.openleg.service.source.LRSBillTextSobiMaker;
import gov.nysenate.openleg.service.spotcheck.base.SpotcheckRunService;
import gov.nysenate.openleg.service.spotcheck.billtext.BillTextReportService;
import gov.nysenate.openleg.service.spotcheck.billtext.BillTextSpotcheckProcessService;
import gov.nysenate.openleg.util.StringDiffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 */
public class BillTextTest extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(BillTextTest.class);

    @Autowired
    SpotcheckRunService runService;

    @Autowired
    BillTextReportService report;

    @Autowired
    BillTextSpotcheckProcessService procService;

    @Autowired
    SqlFsBillTextReferenceDao dao;

    @Autowired
    LRSBillTextSobiMaker sobiMaker;


    @Test
    public void processTest() throws Exception{
        procService.collate();
        procService.ingest();
        // if processing occurred, a report should be run
    }

    @Test
    public void queueThenProcessTest() throws Exception {
        queueTest();
        processTest();
    }

    @Test
    public void runTest() {
        runService.runReports(SpotCheckRefType.LBDC_SCRAPED_BILL);
    }

    @Test
    public void makeSobiTest() {
        List<BaseBillId> billIds = Arrays.asList(
                new BaseBillId("S105", 2015),
                new BaseBillId("A3233", 2015));
        File resultDir = new File("/tmp");
        sobiMaker.makeSobi(billIds, resultDir);
    }
    @Test
    public void getReportTest() {
        LocalDateTime reportDateTime = LocalDateTime.parse("2015-04-29T11:11:13");
        new ReportDetailView<>(
                report.getReport(new SpotCheckReportId(SpotCheckRefType.LBDC_SCRAPED_BILL, reportDateTime)));
    }

    @Test
    public void queueTest() {
        List<BaseBillId> billIds = Arrays.asList(
                new BaseBillId("S5513", 2015));
        billIds.forEach(billId -> dao.addBillToScrapeQueue(billId, ScrapeQueuePriority.MANUAL_ENTRY.getPriority()));
        logger.info("queue is now {}", dao.getScrapeQueue(LimitOffset.ALL, SortOrder.DESC).getResults().stream()
                .map(BillScrapeQueueEntry::getBaseBillId)
                .collect(Collectors.toList()));
    }

/////////////////////////////////////////////////////////////////////////////

    @Test
    public void getBillsTest(){
        BaseBillId id2 = new BaseBillId("S1", 2015);
        List<BillTextReference> list = dao.getBillTextReference(id2);

        for (BillTextReference ref : list){
            System.out.println("________________________________");
            System.out.println("print no:::: "+ ref.getPrintNo());
            System.out.println("Amend:::: "+ ref.getActiveVersion());
            System.out.println("Session Year:::: "+ ref.getSessionYear());
            System.out.println("refDate :::: "+ ref.getReferenceDate());
        }
    }
    @Test
    public void getBillTest(){
        BaseBillId id2 = new BaseBillId("S1", new SessionYear(2015));

        String str =                                              "2015-03-31 12:52:39.986";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

//        BillTextReference ref = dao.getPKBillTextReference(id2, dateTime);
//
//        System.out.println("________________turned to ref object________________");
//        System.out.println("print no:::: "+ ref.getPrintNo());
//        System.out.println("Amend:::: "+ ref.getActiveVersion());
//        System.out.println("refDate:::: "+ ref.getReferenceDate());
//        System.out.println("Session Year:::: "+ ref.getSessionYear());
    }

    /////////////////////////////////////////////////////////////////////////////
    @Test
    public void diffTest() throws Exception {
        String s1= "as";
        String s2  = "asd";
        StringDiffer dif = new StringDiffer();
        LinkedList<StringDiffer.Diff> diffs = dif.diff_main(s1, s2);
        System.out.println(diffs);
    }

    @Test
    public void StringTest() throws Exception {


        String y = "\\n          texttexttext     \\n";
        //String txt = y.replaceAll("\\r\\n|\\r|\\n", " ");
        String txt = y.replaceAll("\\\\n", "");
        System.out.println(txt);
    }
    @Test
    public void findDeletes() throws Exception{
        BufferedReader br = new BufferedReader(new FileReader("~.DeleteStuff.txt"));
        Path path = FileSystems.getDefault().getPath("home/kyle", "DeleteStuff.log");

        ArrayList<String> y = new ArrayList<String>(Files.readAllLines(path)) {
        };

    }
    @Test
    public void jsoupTest() {
        String html =
                "<span id='hi'>" +
                        "some text" +
                        "<u>some more text</u>" +
                        "<notu>texxxxt</notu>" +
                        "even more text" +
                        "</span>";
        Document doc = Jsoup.parse(html);

        Elements eles = doc.getElementById("hi").children();

//        for (Element e : eles) {
//            logger.info(e.text());
//        }

        StringBuilder stringBuilder = new StringBuilder();

        processNode(doc.getElementById("hi"), stringBuilder);

        System.out.println(stringBuilder.toString());

    }

    void processNode(Element ele, StringBuilder stringBuilder) {

        for (Node t : ele.childNodes()) {
            if (t instanceof Element) {
                Element e = (Element) t;
                if (e.tag().getName().equals("u")) {
                    stringBuilder.append(e.text().toUpperCase());
                    stringBuilder.append("\n");
                } else {
                    processNode(e, stringBuilder);
                }
            } else if (t instanceof TextNode) {
                stringBuilder.append(((TextNode) t).text());
                stringBuilder.append("\n");
            }
        }
    }
}


