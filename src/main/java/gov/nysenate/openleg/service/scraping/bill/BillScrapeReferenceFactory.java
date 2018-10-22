package gov.nysenate.openleg.service.scraping.bill;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillType;
import gov.nysenate.openleg.model.spotcheck.billscrape.BillScrapeReference;
import gov.nysenate.openleg.util.BillTextUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BillScrapeReferenceFactory {

    private BillScrapeReferenceHtmlParser htmlParser;

    @Autowired
    public BillScrapeReferenceFactory(BillScrapeReferenceHtmlParser htmlParser) {
        this.htmlParser = htmlParser;
    }

    public BillScrapeReference createFromFile(BillScrapeFile btrFile) throws IOException {
        Document doc = Jsoup.parse(btrFile.getFile(), "UTF-8");
        if (htmlParser.isBillMissing(doc)) {
            return errorBillScrapeReference(btrFile);
        }
        BillId billId = new BillId(htmlParser.parsePrintNo(doc), btrFile.getBaseBillId().getSession());
        String text = htmlParser.parseText(doc);
        text = formatText(text, billId.getBillType());
        // Only parse memo's for non resolutions.
        String memo = billId.getBillType().isResolution() ? "" : htmlParser.parseMemo(doc);
        BillScrapeReference reference = new BillScrapeReference(billId, btrFile.getReferenceDateTime(), text, memo);
        reference.setVotes(htmlParser.parseVotes(doc));
        return reference;
    }

    private String formatText(String text, BillType billType) {
        if (!billType.isResolution()) {
            text = BillTextUtils.formatHtmlExtractedBillText(text);
        }
        return text;
    }

    private BillScrapeReference errorBillScrapeReference(BillScrapeFile btrFile) throws IOException {
        return BillScrapeReference.getErrorBtr(btrFile.getBaseBillId(), btrFile.getReferenceDateTime(),
                FileUtils.readFileToString(btrFile.getFile()));
    }
}
