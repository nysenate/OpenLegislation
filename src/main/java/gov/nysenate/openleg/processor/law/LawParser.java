package gov.nysenate.openleg.processor.law;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.law.LawFragment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class LawParser
{
    /** Set of all law sections that are designated as Court Act laws. */
    private static Set<String> courtActLawSections = Sets.newHashSet("CTC", "FCT", "CCA", "CRC", "SCP", "UCT", "UDC", "UJC");

    /** Set of all law sections that are designated as Rules laws. */
    private static Set<String> rulesLawSections = Sets.newHashSet("CMA", "CMS");

    /** Pattern for law doc headers.  */
    protected static Pattern lawHeader =
        Pattern.compile("\\.\\.SO DOC (\\w{3})(.{12}) (.{8}) (.{15}) (?:LAWS\\(((?:UN?)CONSOLIDATED))");

    /** --- Constructors --- */



    /** --- Methods --- */

    public List<LawFragment> extractLawFragments(String text) {
        List<LawFragment> fragments = new ArrayList<>();
        return fragments;

    }
}