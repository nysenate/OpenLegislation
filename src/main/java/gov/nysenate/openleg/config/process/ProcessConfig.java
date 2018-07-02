package gov.nysenate.openleg.config.process;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import gov.nysenate.openleg.model.sourcefiles.SourceType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiLineType;
import gov.nysenate.openleg.processor.base.AbstractDataProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.sourcefiles.SourceType.SOBI;
import static gov.nysenate.openleg.model.sourcefiles.SourceType.XML;
import static gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType.*;
import static gov.nysenate.openleg.model.sourcefiles.sobi.SobiLineType.*;

@Component
public class ProcessConfig extends AbstractDataProcessor {

    private static HashSet<SobiFragmentType> xmlProcessWhitelist;
    private static HashSet<SobiFragmentType> sobiProcessWhitelist;
    private static HashSet<SobiLineType> sobiBlockWhitelist;
    private RangeMap<Integer, HashSet> yearMap;

    private SourceFilter xmlFilter;
    private SourceFilter sobiFilter;

    //default constructor
    public ProcessConfig() {}

    @PostConstruct
    public void init() {
        xmlProcessWhitelist = createDefaultXmlProcessWhitelist();
        sobiProcessWhitelist = createDefaultSobiProcessWhitelist();
        sobiBlockWhitelist = createDefaultSobiBlockWhitelist();
        xmlFilter = new XmlWhitelistFilter();
        sobiFilter = new SobiWhitelistFilter();

        yearMap = ImmutableRangeMap.<Integer, HashSet>builder()
                .put(Range.lessThan(2009), xmlProcessWhitelist)
                .put(Range.closedOpen(2009, 2018), sobiProcessWhitelist)
                .put(Range.atLeast(2018), xmlProcessWhitelist)
                .build();
    }

    /**
     * This method filters the given list of fragments,
     * returning a list that is filtered according to the process config.
     *
     * @param fragments {@link SobiFragment}
     * @return {@link List<SobiFragment>}
     */
    public List<SobiFragment> filterFileFragments(List<SobiFragment> fragments) {
        List<SobiFragment> allowedFragments = fragments.stream()
                .filter(frag -> {
                    HashSet<SobiFragmentType> fragProcWhitelist = determineProcessWhitelist(frag.getPublishedDateTime());
                    SourceType sourceType = frag.getParentSobiFile().getSourceType();
                    if (sourceType == XML) {
                        return xmlFilter.acceptFragment(frag, fragProcWhitelist);
                    }
                    else if (sourceType == SOBI) {
                        return sobiFilter.acceptFragment(frag, fragProcWhitelist);
                    }
                    throw new IllegalStateException("Unknown source type: " + sourceType);
                })
                .collect(Collectors.toList());
        return allowedFragments;
    }

    /**
     * This method filters the given list of sobi blocks,
     * returning a list that is filtered according to the process config.
     *
     * @param blocks {@link List<SobiBlock>}
     * @return {@link List<SobiBlock>}
     */
    public List<SobiBlock> filterSobiBlocks( List<SobiBlock> blocks) {
        return blocks.stream()
                .filter(block -> !sobiFilter.acceptBlock(block, sobiBlockWhitelist))
                .collect(Collectors.toList());
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


    //Retrieves a single ProcessYear from the processYearMap
    private HashSet<SobiFragmentType> determineProcessWhitelist(LocalDateTime publishDate) {
        return determineProcessWhitelist(LocalDate.of(publishDate.getYear(), publishDate.getMonth(),
                publishDate.getDayOfMonth()));
    }

    public HashSet<SobiFragmentType> determineProcessWhitelist(LocalDate publishDate) {
        return yearMap.get(publishDate.getYear());
    }
}
