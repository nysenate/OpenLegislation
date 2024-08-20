package gov.nysenate.openleg.search.bill;

import com.google.common.base.Stopwatch;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.bill.exception.BillAmendNotFoundEx;
import gov.nysenate.openleg.legislation.bill.exception.BillNotFoundEx;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ElasticBillSearchServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchServiceTests.class);

    @Autowired
    BillDataService billDataService;

    @Autowired
    private ElasticBillSearchDao billSearchDao;

    @Autowired
    private ElasticBillSearchService billSearchService;

    @Test
    public void testSearch() throws Exception {
        billSearchService.searchBills("explore", null, LimitOffset.TEN).resultList()
            .forEach(r -> logger.info("{}", r.result()));
    }

    @Test
    public void testBulkIndex() {
        LimitOffset limitOffset = new LimitOffset(500, 0);
        int billCount;
        do {
            logger.info("Retrieving bills...");
            List<Bill> bills = billDataService.getBillIds(SessionYear.current(), limitOffset).stream()
                    .map(billDataService::getBill)
                    .toList();
            billCount = bills.size();
            if (billCount > 0) {
                logger.info(String.format("Indexing bills %d - %d",
                        limitOffset.offsetStart(), limitOffset.offsetStart() + billCount - 1));
                billSearchDao.updateIndex(bills);
            }
            limitOffset = limitOffset.next();
        }
        while (billCount > 0);
        logger.info("done");
    }

    @Test
    public void testRebuildIndex() {
        billSearchService.rebuildIndex();
    }

    @Test
    public void billsForSessionPerfTest() throws SearchException {
        // Run several times to allow times to converge on lower limit.
        for (int run = 1; run <= 4; run++) {
            Stopwatch sw = Stopwatch.createStarted();
            for (SessionYear s = SessionYear.of(2009); s.compareTo(SessionYear.current()) <= 0;
                 s = s.nextSessionYear()) {
                logger.info("Getting bills for session {}", s);
                int sessionTotal = Integer.MAX_VALUE;
                for (LimitOffset limoff = LimitOffset.HUNDRED; limoff.offsetStart() < sessionTotal; limoff = limoff.next()) {
                    SearchResults<BaseBillId> results = billSearchService.searchBills(s, "publishedDateTime:asc", limoff);
                    sessionTotal = results.totalResults();
                }
            }
            logger.info("Run {}: Iterated through all sessions in {}.", run, sw.stop());
        }
    }

    @Test
    public void randomSearchPerfTest() throws SearchException {
        int termCount = 500;
        logger.info("Generating {} terms...", termCount);
        List<String> terms = getRandomBillSearchTerms(termCount);
        logger.info("Performing searches...");
        for (int run = 1; run <= 4; run++) {
            Stopwatch sw = Stopwatch.createStarted();
            for (String term : terms) {
                billSearchService.searchBills(term, "", LimitOffset.FIFTY);
            }
            logger.info("Run {}: Performed {} bill searches in {}.", run, termCount, sw.stop());
        }
    }


    private List<String> getRandomBillSearchTerms(int count) {
        Random rand = new Random(12345678910L);

        List<String> terms = new LinkedList<>();
        List<BaseBillId> billIds = billDataService.getBillIds(SessionYear.of(2017), LimitOffset.ALL);
        for (int i = 0; i < count; i++) {
            int billIndex = rand.nextInt(billIds.size());
            BaseBillId billId = billIds.get(billIndex);
            try {
                Bill bill = billDataService.getBill(billId);
                String text;
                int field = rand.nextInt(3);
                switch (field) {
                    case 0:
                        text = bill.getTitle();
                        break;
                    case 1:
                        text = bill.getFullTextPlain();
                        break;
                    case 2:
                        text = bill.getStatus().toString();
                        break;
                    default:
                        text = "excelsior";
                }
                if (StringUtils.isBlank(text)) {
                    terms.add("*");
                    continue;
                }
                int termStart = rand.nextInt(text.length());
                int maxTermLength = Integer.min(100, text.length() - termStart);
                int termLength = rand.nextInt(maxTermLength);
                String term = StringUtils.substring(text, termStart, termStart + termLength);
                term = term.replaceAll("[^\\w]+", " ").toLowerCase();
                terms.add("\"" + term + "\"");
            } catch (BillNotFoundEx | BillAmendNotFoundEx ex) {
                logger.info("hMMM {}", billId);
                terms.add("*");
            }
        }
        return terms;
    }
}
