package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiLineType;

import java.util.HashSet;

import static gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType.*;
import static gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType.BILLTEXT;
import static gov.nysenate.openleg.model.sourcefiles.sobi.SobiLineType.*;

public class SobiWhitelistFilter implements SourceFilter {

    private HashSet<SobiFragmentType> sobiProcessWhitelist = createDefaultSobiProcessWhitelist();
    private HashSet<SobiLineType> sobiBlockWhitelist = createDefaultSobiBlockWhitelist();

    public boolean acceptFragment(SobiFragment sobiFragment) {
        return sobiProcessWhitelist.contains(sobiFragment.getType());
    }

    public boolean acceptBlock(SobiBlock sobiBlock) {
        return sobiBlockWhitelist.contains(sobiBlock.getType());
    }

    //Creates the default sobi only process years
    private HashSet<SobiFragmentType> createDefaultSobiProcessWhitelist() {
        HashSet<SobiFragmentType> sobiProcessWhitelist = new HashSet<>();
        sobiProcessWhitelist.add(BILL);
        sobiProcessWhitelist.add(AGENDA);
        sobiProcessWhitelist.add(AGENDA_VOTE);
        sobiProcessWhitelist.add(CALENDAR);
        sobiProcessWhitelist.add(CALENDAR_ACTIVE);
        sobiProcessWhitelist.add(COMMITTEE);
        sobiProcessWhitelist.add(ANNOTATION);
        sobiProcessWhitelist.add(BILLTEXT);
        return sobiProcessWhitelist;
    }

    private HashSet<SobiLineType> createDefaultSobiBlockWhitelist() {
        HashSet<SobiLineType> sobiBlockWhitelist = new HashSet<>();
        sobiBlockWhitelist.add(BILL_INFO);
        sobiBlockWhitelist.add(LAW_SECTION);
        sobiBlockWhitelist.add(TITLE);
        sobiBlockWhitelist.add(BILL_EVENT);
        sobiBlockWhitelist.add(SAME_AS);
        sobiBlockWhitelist.add(CO_SPONSOR);
        sobiBlockWhitelist.add(MULTI_SPONSOR);
        sobiBlockWhitelist.add(PROGRAM_INFO);
        sobiBlockWhitelist.add(ACT_CLAUSE);
        sobiBlockWhitelist.add(LAW);
        sobiBlockWhitelist.add(SUMMARY);
        sobiBlockWhitelist.add(SPONSOR_MEMO);
        sobiBlockWhitelist.add(RESOLUTION_TEXT);
        sobiBlockWhitelist.add(TEXT);
        sobiBlockWhitelist.add(VETO_APPROVE_MEMO);
        sobiBlockWhitelist.add(VOTE_MEMO);
        return sobiBlockWhitelist;
    }
}
