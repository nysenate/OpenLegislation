package gov.nysenate.openleg.service.scraping;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kyle on 3/10/15.
 */
@Service
public class ScrapedBillTextParser {

    private static final Pattern scrapedBillFilePattern = Pattern.compile("^(\\d{4})-([A-z]\\d+)-(\\d{8}T\\d{6}).html$");

    private static final Pattern billIdPattern = Pattern.compile("^([A-z]\\d+)(?:-([A-z]))?$");

    private static final Pattern resolutionStartPattern = Pattern.compile("^\\s+([A-z]{2,})");

    /**
     * Parses a scraped bill file into a bill text reference containing an active amendment, full text, and a sponsor memo
     * @param file File
     * @return BillTextReference
     * @throws IOException if there are troubles reading the file
     * @throws ParseError if there are troubles while parsing the file
     */
    public BillTextReference parseReference(File file) throws IOException, ParseError{
        Matcher filenameMatcher = scrapedBillFilePattern.matcher(file.getName());
        if (filenameMatcher.matches()) {
            // Parse metadata from the file name
            BaseBillId baseBillId = new BaseBillId(filenameMatcher.group(2), Integer.parseInt(filenameMatcher.group(1)));
            LocalDateTime referenceDateTime = LocalDateTime.parse(filenameMatcher.group(3), DateUtils.BASIC_ISO_DATE_TIME);

            Document document = Jsoup.parse(file, "UTF-8");
            // If the scraped page indicates the bill was not found, return a "not found" bill text reference
            if (billNotFound(document)) {
                return new BillTextReference(baseBillId, referenceDateTime, FileUtils.readFileToString(file), "", true);
            }
            try {
                // Get the active amendment id, full text and memo
                BillId billId = getBillId(document, baseBillId.getSession());
                String text = getText(document, baseBillId);
                String memo = getMemo(document, baseBillId);
                return new BillTextReference(billId, referenceDateTime, text, memo, false);
            } catch (ParseError ex) {
//                throw new ParseError("Error while parsing scraped bill: " + file.getName(), ex);
                return new BillTextReference(baseBillId, referenceDateTime, "", "", true);
            }
        }
        throw new ParseError("Could not parse scraped bill filename: " + file.getName());
    }

    /** --- Internal Methods --- */

    /**
     * Parses the amendment bill id from one of the first header lines
     */
    private BillId getBillId(Document document, SessionYear sessionYear) throws ParseError {
        Element printNoEle = document.select("span.nv_bot_info > strong").first();
        if (printNoEle != null) {
            Matcher printNoMatcher = billIdPattern.matcher(printNoEle.text());
            if (printNoMatcher.matches()) {
                String basePrintNo = printNoMatcher.group(1);
                String version = printNoMatcher.group(2);
                return new BillId(basePrintNo + (version != null ? version : ""), sessionYear);
            }
            throw new ParseError("could not parse scraped bill print no: " + printNoEle.text());
        }
        throw new ParseError("could not get scraped bill print no:");
    }

    /**
     * Parses the full bill text and formats it to account for standard differences between LRS and sobi data
     */
    private String getText(Document document, BaseBillId baseBillId) throws ParseError {
        Element contents = document.getElementById("nv_bot_contents");
        if (contents == null) {
            throw new ParseError("Could not locate scraped bill contents");
        }
        Elements textEles = new Elements();

        // Bill text is found in all pre tags contained in <div id="nv_bot_contents"> before the first <hr class="noprint">
        for (Element element : contents.children()) {
            if ("pre".equalsIgnoreCase(element.tagName())) {
                textEles.add(element);
            } else if ("hr".equalsIgnoreCase(element.tagName()) && element.classNames().contains("noprint")) {
                break;
            }
        }

        StringBuilder textBuilder = new StringBuilder();

        textEles.forEach(ele -> processTextNode(ele, textBuilder));

        return formatBillText(textBuilder.toString(), baseBillId);
    }

    /**
     * Alters the raw bill text to match the standard formatting of sobi bill text
     */
    private String formatBillText(String billText, BaseBillId billId) {
        billText = billText.replaceAll("[\r\\uFEFF-\\uFFFF]|(?<=\n) ", "");
        billText = billText.replaceAll("§", "S");
        if (billId.getBillType().isResolution()) {
            billText = billText.replaceFirst("^\n\n[\\w \\.-]+\n\n[\\w '\\.\\-:]+\n", "");
            billText = billText.replaceFirst("^\\s+PROVIDING", String.format("\n%s RESOLUTION providing", billId.getChamber()));
            Matcher resoStartMatcher = resolutionStartPattern.matcher(billText);
            if (resoStartMatcher.find()) {
                billText = billText.replaceFirst(resolutionStartPattern.pattern(),
                        "\nLEGISLATIVE RESOLUTION " + resoStartMatcher.group(1).toLowerCase());
            }
        } else {
            billText = billText.replaceFirst("^\n\n[ ]{12}STATE OF NEW YORK(?=\n)",
                    "\n                           S T A T E   O F   N E W   Y O R K");
            billText = billText.replaceFirst("(?<=\\n)[ ]{16}IN SENATE(?=\\n)",
                    "                                   I N  S E N A T E");
            billText = billText.replaceFirst("(?<=\\n)[ ]{15}IN ASSEMBLY(?=\\n)",
                    "                                 I N  A S S E M B L Y");
            billText = billText.replaceFirst("(?<=\\n)[ ]{12}SENATE - ASSEMBLY(?=\\n)",
                    "                             S E N A T E - A S S E M B L Y");
        }
        return billText;
    }

    /**
     * Parses and returns the sponsor memo
     */
    private String getMemo(Document document, BaseBillId baseBillId) {
        Element memoEle = document.select("pre:last-of-type").first(); // you are the first and last of your kind
        // Do not get memo if bill is a resolution
        if (!baseBillId.getBillType().isResolution() && memoEle != null) {
            StringBuilder memoBuilder = new StringBuilder();
            processTextNode(memoEle, memoBuilder);
            // todo format text
            return memoBuilder.toString();
        }
        return "";
    }

    /**
     * Extracts bill/memo text from an element recursively
     */
    private void processTextNode(Element ele, StringBuilder stringBuilder) {
        for (Node t : ele.childNodes()) {
            if (t instanceof Element) {
                Element e = (Element) t;
                // TEXT IN <U> TAGS IS REPRESENTED IN CAPS FOR SOBI BILL TEXT
                if ("u".equals(e.tag().getName())) {
                    stringBuilder.append(e.text().toUpperCase());
                } else {
                    processTextNode(e, stringBuilder);
                }
            } else if (t instanceof TextNode) {
                stringBuilder.append(((TextNode) t).getWholeText());
            }
        }
    }

    /**
     * Returns true if a "Bill Status Information Not Found" tag is located in the document indicating that
     * the bill is not on LRS
     */
    private boolean billNotFound(Document document) {
        Element botContents = document.getElementById("nv_bot_contents");
        if (botContents == null) return true;
        Elements redFonts = botContents.select("font[color=\"red\"]");
        Element notFoundText = redFonts.first();
        return notFoundText != null && "Bill Status Information Not Found".equals(notFoundText.text());
    }
}
