package gov.nysenate.openleg.processor.daybreak;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakDocType;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakFile;
import gov.nysenate.openleg.model.spotcheck.daybreak.PageFileEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class DaybreakPageFileParser {

    private static Logger logger = LoggerFactory.getLogger(DaybreakFileParser.class);

    private static String publishDateMatchPattern = "MM/dd/yyyy";

    /**
     * Parses a page daybreak file into PageFileEntries
     * @param daybreakFile
     * @return
     * @throws IOException
     */
    public static List<PageFileEntry> extractPageFileEntries(DaybreakFile daybreakFile) throws IOException{
        Assert.isTrue(daybreakFile.getDaybreakDocType() == DaybreakDocType.PAGE_FILE, "this method only parses page files");

        List<PageFileEntry> pageFileEntries = new ArrayList<>();

        // Get file full text
        List<String> lines = FileUtils.readLines(daybreakFile.getFile(), "latin1");
        lines.remove(0); // Remove the header line

        // Extract a PageFileEntry from each line
        lines.stream()
                .filter(line -> !line.trim().isEmpty())
                .forEach(line -> pageFileEntries.add(getPageFileEntryFromLine(line, daybreakFile)));

        return pageFileEntries;
    }

    /**
     * Converts a line of text into a PageFileEntry object
     * @param line
     * @param daybreakFile
     * @return
     */
    private static PageFileEntry getPageFileEntryFromLine(String line, DaybreakFile daybreakFile){
        String[] parts = line.split(",");
        // Page file line format
        // SESSYR,SEN_HSE,SEN_NO,SEN_AMD,ASM_HSE,ASM_NO,ASM_AMD,OUT_DATE,PAGES

        SessionYear sessionYear = null;
        try {
            sessionYear = SessionYear.of(Integer.parseInt(parts[0]));
        }
        catch(NumberFormatException ex){
            logger.error(ex.getMessage());
        }
        LocalDate publishDate = null;
        try{
            publishDate = LocalDateTime.ofInstant(
                    DateUtils.parseDateStrictly(parts[7], publishDateMatchPattern).toInstant(), ZoneId.systemDefault()
            ).toLocalDate();
        }
        catch(ParseException ex){
            logger.error("Could not parse PageFileEntry publish date " + parts[7]);
        }
        int pages = Integer.parseInt(parts[8]);

        String sen_id = (parts[1]+parts[2].replaceAll("^0*", "")+parts[3]).trim();
        String asm_id = (parts[4]+parts[5].replaceAll("^0*", "")+parts[6]).trim();
        BillId senateBillId = null;
        BillId assemblyBillId = null;
        if(!sen_id.isEmpty()) {
            senateBillId = new BillId(sen_id, sessionYear);
        }
        if(!asm_id.isEmpty()) {
            assemblyBillId = new BillId(asm_id, sessionYear);
        }

        return new PageFileEntry(senateBillId, assemblyBillId, daybreakFile, publishDate, pages);
    }
}
