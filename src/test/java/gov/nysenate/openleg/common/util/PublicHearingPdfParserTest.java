package gov.nysenate.openleg.common.util;


import gov.nysenate.openleg.BaseTests;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.config.annotation.IntegrationTest;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

// TODO: Make into unit test. Stop extending.
@Category(IntegrationTest.class)
public class PublicHearingPdfParserTest extends BaseTests {
    @Autowired
    private PublicHearingDataService hearings;

    @Test
    public void testHearings() {
        var pageLengthCount = new HashMap<Integer, Integer>();
        var ids = hearings.getPublicHearingIds(SortOrder.ASC, LimitOffset.ALL);
        for (var id : ids) {
            PublicHearing hearing = hearings.getPublicHearing(id);
            var pages = PublicHearingTextUtils.getPages(hearing.getText());
            for (int i = 0; i < pages.size(); i++) {
                var page = pages.get(i);
                pageLengthCount.put(page.size(), pageLengthCount.getOrDefault(page.size(), 0) + 1);
                if (page.size() == 51 && !page.get(0).trim().equals(String.valueOf(i + 1)))
                    System.err.println("51 problem");
                if (page.size() == 52)
                    System.err.println(id.getFileName() + ", " + (i + 1));
            }
        }
        System.out.println(pageLengthCount);
    }
}
