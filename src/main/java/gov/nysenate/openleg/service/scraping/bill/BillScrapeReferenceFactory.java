package gov.nysenate.openleg.service.scraping.bill;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillText;
import gov.nysenate.openleg.model.bill.BillTextFormat;
import gov.nysenate.openleg.model.bill.BillType;
import gov.nysenate.openleg.model.spotcheck.billscrape.BillScrapeReference;
import gov.nysenate.openleg.processor.bill.BillTextDiffProcessor;
import gov.nysenate.openleg.util.BillTextUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class BillScrapeReferenceFactory {

    private BillScrapeReferenceHtmlParser htmlParser;
    private BillTextDiffProcessor textDiffProcessor;

    @Autowired
    public BillScrapeReferenceFactory(BillScrapeReferenceHtmlParser htmlParser, BillTextDiffProcessor textDiffProcessor) {
        this.htmlParser = htmlParser;
        this.textDiffProcessor = textDiffProcessor;
    }

    public BillScrapeReference createFromFile(BillScrapeFile btrFile) throws IOException {
        Document doc = Jsoup.parse(btrFile.getFile(), "UTF-8");
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false)); // Preserve whitespace.

        if (htmlParser.isBillMissing(doc)) {
            return errorBillScrapeReference(btrFile);
        }
        BillId billId = new BillId(htmlParser.parsePrintNo(doc), btrFile.getBaseBillId().getSession());
        Elements billTextElements = htmlParser.getBillTextElements(doc);
        String htmlText = billTextElements.outerHtml();

        BillText billText = textDiffProcessor.processBillText(htmlText);
        String plain = billText.getFullText(BillTextFormat.PLAIN);
        htmlText = billText.getFullText(BillTextFormat.HTML);

        // Only parse memo's for non resolutions.
        String memo = billId.getBillType().isResolution() ? "" : htmlParser.parseMemo(doc);
        BillScrapeReference reference = new BillScrapeReference(billId, btrFile.getReferenceDateTime(), plain, htmlText, memo);
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
                FileUtils.readFileToString(btrFile.getFile(), Charset.defaultCharset()));
    }
}
