package gov.nysenate.openleg.service.scraping;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillType;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.util.BillTextUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BillTextReferenceFactory {

    private BillTextReferenceHtmlParser btrHtmlParser;
    private LrsToSobiBillText lrsToSobiBillText;

    @Autowired
    public BillTextReferenceFactory(BillTextReferenceHtmlParser btrHtmlParser, LrsToSobiBillText lrsToSobiBillText) {
        this.btrHtmlParser = btrHtmlParser;
        this.lrsToSobiBillText = lrsToSobiBillText;
    }

    public BillTextReference createFromFile(BillTextReferenceFile btrFile) throws IOException, LrsOutageScrapingEx {
        Document doc = Jsoup.parse(btrFile.getFile(), "UTF-8");
        if (btrHtmlParser.isLrsOutage(doc)) {
            throw new LrsOutageScrapingEx(btrFile.getBaseBillId());
        }
        if (btrHtmlParser.isBillMissing(doc)) {
            return errorBillTextReference(btrFile);
        }
        BillId billId = new BillId(btrHtmlParser.parsePrintNo(doc), btrFile.getBaseBillId().getSession());
        String text = btrHtmlParser.parseText(doc);
        text = formatText(text, billId.getBillType());
        String memo = billId.getBillType().isResolution() ? "" : btrHtmlParser.parseMemo(doc); // Only parse memo's for non resolutions.
        return new BillTextReference(billId, btrFile.getReferenceDateTime(), text, memo);
    }

    private String formatText(String text, BillType billType) {
        if (billType.isResolution()) {
            return lrsToSobiBillText.resolutionText(text, billType.getChamber());
        }
        return BillTextUtils.formatHeader(text);
    }

    private BillTextReference errorBillTextReference(BillTextReferenceFile btrFile) throws IOException {
        return BillTextReference.getErrorBtr(btrFile.getBaseBillId(), btrFile.getReferenceDateTime(),
                FileUtils.readFileToString(btrFile.getFile()));
    }
}
