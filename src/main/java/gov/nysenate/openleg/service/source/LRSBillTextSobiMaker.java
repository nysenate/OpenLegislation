package gov.nysenate.openleg.service.source;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.service.scraping.BillTextScraper;
import gov.nysenate.openleg.service.scraping.ScrapedBillTextParser;
import gov.nysenate.openleg.util.FileIOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that can be used to generate patch bill text sobis
 */
@Service
public class LRSBillTextSobiMaker {

    private static final Logger logger = LoggerFactory.getLogger(LRSBillTextSobiMaker.class);

    private static final String sobiDocTemplate =
            "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<PATCH>\n" +
            "Copied LRS bill text for ${billIds}\n" +
            "</PATCH>\n" +
            "<DATAPROCESS TIME=\"${pubDateTime}\">\n" +
            "${data}" +
            "</DATAPROCESS>\n" +
            "<SENATEDATA TIME=\"${pubDateTime}\">\n" +
            "No data to process on ${pubDate} at ${pubTime}\n" +
            "</SENATEDATA>";

    private static final DateTimeFormatter sobiFileNameFormat =
            DateTimeFormatter.ofPattern("'SOBI.D'yyMMdd.'T'HHmmss.'TXT'");

    private static final DateTimeFormatter pubDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss");
    private static final DateTimeFormatter pubDateFormat = DateTimeFormatter.ofPattern("dd/mm/yyyy");
    private static final DateTimeFormatter pubTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final String billTextHeaderTemplateTemplate =
            "00000.SO DOC ${printNo}%s                    BTXT            ${year}";
    private static final String billTextHeaderTemplate = String.format(billTextHeaderTemplateTemplate, "          ");
    private static final String billTextCloserTemplate = String.format(billTextHeaderTemplateTemplate, "*END*     ");

    @Autowired BillTextScraper billTextScraper;
    @Autowired ScrapedBillTextParser billTextParser;

    private File scrapedDir = new File("/tmp/scraped-bills");

    /**
     * Attempts to scrape bill text for the given bill ids
     * Formats the scraped bill texts into a sobi file written to the result dir
     * @param billIds Collection<BaseBillId> - bill ids to be scraped
     * @param resultDir File - the directory to save the generated sobi
     */
    public void makeSobi(Collection<BaseBillId> billIds, File resultDir) {
        try {
            scrapeBills(billIds);

            List<BillTextReference> btrs = parseBills();

            StringBuilder dataBuilder = new StringBuilder();
            for (BillTextReference btr : btrs) {
                addBillText(btr, dataBuilder);
            }

            writeSobi(dataBuilder, resultDir, btrs.stream().map(BillTextReference::getBillId).collect(Collectors.toList()));
        } catch (IOException ex) {
            logger.error("Error while generating sobis \n{}", ex);
        }
    }

    /**
     * Appends sobi formatted bill text from the given bill text reference to the given string builder
     */
    private void addBillText(BillTextReference btr, StringBuilder dataBuilder) {
        logger.info("formatting {}", btr.getBillId());
        BillId billId = btr.getBillId();

        // Format the print no portion of the header
        String headerPrintNo = String.format("%s %d%s", billId.getBillType(), billId.getNumber(), billId.getVersion());
        int length = headerPrintNo.length();
        for (int i = 0; i < 16 - length; i++) {
            headerPrintNo += " ";
        }

        // Format the header
        String yearString = Integer.toString(btr.getSessionYear());
        String header = StrSubstitutor.replace(billTextHeaderTemplate,
                ImmutableMap.of("printNo", headerPrintNo, "year", yearString));

        // Format the line start for this bill
        String lineStart = String.format("%s%s%05d%sT", yearString, billId.getBillType(), billId.getNumber(),
                billId.getVersion() != Version.DEFAULT ? billId.getVersion() : " ");

        int textLine = 1;   // Tracks text line numbers
        int totalLine = 0;  // Tracks total lines that have been added including headers

        for (String line : btr.getText().split("\n")) {
            dataBuilder.append(lineStart);
            if (totalLine % 100 == 0) {     // Add a header every 100 lines
                dataBuilder.append(header)
                        .append("\n")
                        .append(lineStart);
                totalLine++;
            }
            // Append line data
            dataBuilder.append(String.format("%05d", textLine));
            dataBuilder.append(line);
            dataBuilder.append("\n");
            textLine++;
            totalLine++;
        }
        // Add a closing header
        dataBuilder.append(lineStart)
                .append(StrSubstitutor.replace(billTextCloserTemplate,
                        ImmutableMap.of("printNo", headerPrintNo, "year", yearString)))
                .append("\n");
    }

    /**
     * Downloads LRS html bill files for the given bill ids
     */
    private void scrapeBills(Collection<BaseBillId> billIds) throws IOException {
        FileUtils.forceMkdir(scrapedDir);
        for (BaseBillId billId : billIds) {
            logger.info("scraping {}", billId);
            billTextScraper.scrapeBill(billId, scrapedDir);
        }
    }

    /**
     * Parses all bill html files in the scraped directory into BillTextReferences
     */
    private List<BillTextReference> parseBills() throws IOException {
        Collection<File> scrapedBills = FileIOUtils.safeListFiles(scrapedDir, false, new String[]{});
        List<BillTextReference> btrs = new ArrayList<>();
        for (File scrapedFile : scrapedBills) {
            try {
                logger.info("parsing {}", scrapedFile);
                btrs.add(billTextParser.parseReference(scrapedFile));
                scrapedFile.delete();
            } catch (ParseError ex) {
                logger.error("error parsing scraped bill file {}:\n{}", scrapedFile, ex);
            }
        }
        return btrs;
    }

    /**
     * Formats the given text data into a sobi file format and saves it to the destination dir
     */
    private void writeSobi(StringBuilder data, File destinationDir, Collection<BillId> billIds) throws IOException {
        LocalDateTime pubDateTime = LocalDateTime.now();
        String fileContents = StrSubstitutor.replace(sobiDocTemplate,
                ImmutableMap.of("data", data.toString(), "pubDateTime", pubDateTime.format(pubDateTimeFormat),
                        "pubDate", pubDateTime.format(pubDateFormat), "pubTime", pubDateTime.format(pubTimeFormat),
                        "billIds", StringUtils.join(billIds, ", ")));
        FileUtils.forceMkdir(destinationDir);
        File sobiFile = new File(destinationDir, pubDateTime.format(sobiFileNameFormat));
        logger.info("writing {}", sobiFile);
        FileUtils.write(sobiFile, fileContents, "UTF-8");
    }
}
