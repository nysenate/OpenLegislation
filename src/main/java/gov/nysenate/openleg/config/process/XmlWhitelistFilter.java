package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiLineType;

import java.util.HashSet;

import static gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType.*;

public class XmlWhitelistFilter implements SourceFilter {

    private HashSet<SobiFragmentType> xmlProcessWhitelist = createDefaultXmlProcessWhitelist();

    public boolean acceptFragment(SobiFragment sobiFragment) {
        return xmlProcessWhitelist.contains(sobiFragment.getType());
    }

    public boolean acceptBlock(SobiBlock sobiBlock) {
        return false;
    }

    //Creates the default Xml only process years
    private HashSet<SobiFragmentType> createDefaultXmlProcessWhitelist() {
        HashSet<SobiFragmentType> xmlProcessYear = new HashSet<>();
        xmlProcessYear.add(ANACT);
        xmlProcessYear.add(APPRMEMO);
        xmlProcessYear.add(BILLSTAT);
        xmlProcessYear.add(BILLTEXT);
        xmlProcessYear.add(LDSUMM);
        xmlProcessYear.add(LDSPON);
        xmlProcessYear.add(LDBLURB);
        xmlProcessYear.add(SAMEAS);
        xmlProcessYear.add(SENMEMO);
        xmlProcessYear.add(SENFLVOTE);
        xmlProcessYear.add(CALENDAR);
        xmlProcessYear.add(CALENDAR_ACTIVE);
        xmlProcessYear.add(AGENDA);
        xmlProcessYear.add(SENAGENV);
        xmlProcessYear.add(VETOMSG);
        xmlProcessYear.add(COMMITTEE);
        return xmlProcessYear;
    }
}
