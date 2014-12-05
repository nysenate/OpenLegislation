package gov.nysenate.openleg.processor.bill;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import gov.nysenate.openleg.model.law.LawChapterCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

public class BillLawCodeParser
{
    private static final Logger logger = LoggerFactory.getLogger(BillLawCodeParser.class);

    private String sectionChar = "§";

    /** --- Input --- */

    private String lawCode;

    /** --- Output --- */

    private Map<LawChapterCode, String> mapping = new HashMap<>();

    /** --- Constructor --- */

    public BillLawCodeParser(String lawCode) {
        this.lawCode = lawCode;
    }

    /** --- Methods --- */

    public void parse() {
        // Law codes are usually delimited by semi-colons for each affected law
        List<String> lawCodes = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(lawCode);
        for (String singleLawCode : lawCodes) {
            logger.info("{}", singleLawCode);

            LinkedList<String> codeParts = new LinkedList<>(
                Splitter.on(Pattern.compile(",")).trimResults().omitEmptyStrings().splitToList(singleLawCode));

            // Sometimes laws are amended generally which alters how we look up the citation codes.
            boolean generally = false;
            if (codeParts.getLast().equalsIgnoreCase("generally")) {
                generally = true;
                codeParts.removeLast();
                String citation = codeParts.removeLast();
                codeParts.addLast(citation.replaceAll("(?i)^Amd ", ""));
            }
            // Determine the law chapter type using the citation code.
            Optional<LawChapterCode> lawChapterCode = LawChapterCode.lookupCitation(codeParts.removeLast());
            if (lawChapterCode.isPresent()) {
                logger.info("{}", lawChapterCode.get() + (generally ? " Generally " : ""));
                mapping.put(lawChapterCode.get(), Joiner.on(" ").join(codeParts));
//                for (String codePart : codeParts) {
//                    LinkedList<String> details = new LinkedList<>(
//                        Splitter.onPattern("(\\s|,)").trimResults().omitEmptyStrings().splitToList(codePart));
//                    Optional<LawActionType> action = LawActionType.lookupAction(details.removeFirst());
//                    if (action.isPresent()) {
//                        logger.info("{} this {}", action, details);
//                    }
//                }
            }
            logger.info("{}", mapping);
        }
    }

    /** --- Internal Methods --- */
}
