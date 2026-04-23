package gov.nysenate.openleg.legislation.bill;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.SillyTest;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.dao.BillDao;
import gov.nysenate.openleg.legislation.law.LawActionType;
import gov.nysenate.openleg.processors.bill.BillLawCodeParser;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

@Category(SillyTest.class)
public class BillRelatedLawsTest extends BaseTests {
    private final Map<LawActionType, TreeSet<String>> mapping = new EnumMap<>(LawActionType.class);

    @Autowired
    private BillDao dao;

    @Test
    public void processedBillsTest() {
        // each session year adds around 2 minutes of runtime
        int[] sessionYears = {2015, 2017, 2019, 2021, 2023};
        for (int sessionYear : sessionYears) {
            var billIds = dao.getBillIds(new SessionYear(sessionYear), LimitOffset.ALL, SortOrder.ASC);
            for (BillId id : billIds) {
                Bill bill = dao.getBill(id);
                Version version = bill.getActiveVersion();
                BillAmendment billAmendment = bill.getAmendment(version);

                boolean skip = false;
                String lawCode = billAmendment.getLawCode();
                for (var entry : billAmendment.getRelatedLawsMap().entrySet()) {
                    // ignore misnamed actions (RENAME->RELETTER and RENUMERATE->RENUMBER)
                    if (entry.getKey().equals("RENAME") || entry.getKey().equals("RENUMERATE")) {
                        skip = true;
                        break;
                    }
                    LawActionType action = LawActionType.valueOf(entry.getKey());
                    put(action, entry.getValue().toArray(new String[0]));
                }
                if (skip) continue;
                compareToLawCode(lawCode);
            }
        }
    }

    private void compareToLawCode(String lawCode) {
        String actual = BillLawCodeParser.parse(lawCode, true);
        String expected = new Gson().toJson(mapping);
        // ignore bills that were previously unprocessed or misprocessed
        if (!expected.equals("{}")) {
            assertEquals(expected, actual);
        }
        mapping.clear();
    }

    private void put(LawActionType action, String... elements) {
        mapping.put(action, Sets.newTreeSet(Arrays.asList(elements)));
    }
}
