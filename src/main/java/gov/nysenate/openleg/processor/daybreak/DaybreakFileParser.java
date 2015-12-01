package gov.nysenate.openleg.processor.daybreak;

import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakDocType;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakFile;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakFragment;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DaybreakFileParser {

    private static final Logger logger = LoggerFactory.getLogger(DaybreakFileParser.class);

    /** Matches a single row in the bill table */
    public static Pattern rowPattern = Pattern.compile("<tr.*?>(.+?)</tr>");
    /** Used to remove undesired html and unwanted sections */
    public static Pattern stripParts = Pattern.compile(
        "<b>(.*?)</b>|" +                   // Remove bold text
        "<(a|/a|td).*?>|" +                 // Remove a, /a, and td tags. Leave /td for later
        "<br>\\s*Criminal Sanction Impact." // Remove criminal impact text if present
    );

    /**
     * Parses through an html daybreak file and extracts any found daybreak fragments
     * @param daybreakFile
     * @return
     */
    public static List<DaybreakFragment> extractDaybreakFragments(DaybreakFile daybreakFile) throws IOException{
        Assert.isTrue(daybreakFile.getDaybreakDocType() != DaybreakDocType.PAGE_FILE, "This parser is not for page files");

        List<DaybreakFragment> daybreakFragments = new ArrayList<>();

        String fullText = FileUtils.readFileToString(daybreakFile.getFile(), "UTF-8").replaceAll("\\r?\\n", " ");

        Matcher rowMatcher = rowPattern.matcher(fullText);
        rowMatcher.find(); // Throw the first two rows away
        rowMatcher.find(); // They are just headers for the table
        while(rowMatcher.find()){   // Each row contains 1 bill
            String text = stripParts.matcher(rowMatcher.group(1))	// Match all non <br> and </td> tags
                    .replaceAll("")				// Remove them
                    .replace("</td>", "\n") 	// convert </td> and <br> to newlines
                    .replace("<br>", "\n")
                    .replace("�", " ")          // Replace all instances of � with space
                    ;

            // Here we are going through each line and trimming excess whitespace
            String[] lines = text.split("\\n");
            String fragmentPrintNo = null;
            StringBuilder fragmentText = new StringBuilder();
            fragmentText.ensureCapacity(text.length());
            for(int i=0; i<lines.length; i++){
                if(i==0){ // The first line should be the bill print number
                    fragmentPrintNo = lines[i].trim();
                }
                fragmentText.append(lines[i].trim());
                fragmentText.append('\n');
            }

            // TODO: it is assumed that the daybreak only contains bills from the current session year
            // todo: perhaps there is another way of getting the session year?
            BillId fragmentBillId = new BillId(fragmentPrintNo, SessionYear.of(daybreakFile.getReportDate().getYear()));

            daybreakFragments.add(new DaybreakFragment(fragmentBillId, daybreakFile, fragmentText.toString()));
        }

        return daybreakFragments;
    }
}
