package gov.nysenate.openleg.processors.config;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.sobi.SobiBlock;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

/**
 * Coordinates data fragment selection from multiple data sources (SOBI and XML)
 *
 * Determines whether or not a fragment should be processed based on its data source and publish date time.
 * Also does the same for SOBI blocks for time periods where some SOBI blocks are processed and others aren't.
 */
@Component
public class ProcessConfig {

    /** The time when we first started receiving sobi files */
    private static final LocalDateTime sobisStart = Year.of(DateUtils.LEG_DATA_START_YEAR).atDay(1).atStartOfDay();

    /** A time just before we first started receiving xml data dumps */
    private static final LocalDateTime xmlDumpStart = LocalDateTime.parse("2017-08-30T12:02:00");

    /** A time just after the 2017 xml data dumps were complete */
    private static final LocalDateTime xmlDumpComplete = LocalDateTime.parse("2017-09-11T10:00:00");

    /** A time when the floor vote data was fixed and the corrected vote data dump had just finished. */
    private static final LocalDateTime xmlFloorVotesFixed = LocalDateTime.parse("2018-03-08T08:35:00");

    private final RangeMap<LocalDateTime, SourceFilter> filterRangeMap;

    public ProcessConfig() {
        filterRangeMap = ImmutableRangeMap.<LocalDateTime, SourceFilter>builder()
                .put(Range.lessThan(sobisStart), new XmlOnlyWhitelistFilter())
                .put(Range.closedOpen(sobisStart, xmlDumpStart), new SobiOnlyWhitelistFilter())
                .put(Range.closedOpen(xmlDumpStart, xmlDumpComplete), new XmlDumpWhitelistFilter())
                .put(Range.closedOpen(xmlDumpComplete, xmlFloorVotesFixed), new XmlNoFlvotWhitelistFilter())
                .put(Range.atLeast(xmlFloorVotesFixed), new XmlOnlyWhitelistFilter())
                .build();
    }

    /**
     * This method filters the given list of fragments,
     * returning a list that is filtered according to the process config.
     *
     * @param fragments {@link LegDataFragment}
     * @return {@link List< LegDataFragment >}
     */
    public List<LegDataFragment> filterFileFragments(List<LegDataFragment> fragments) {
        return fragments.stream()
                .filter(this::acceptFragment)
                .toList();
    }

    /**
     * This method filters the given list of sobi blocks,
     * returning a list that is filtered according to the process config.
     *
     * @param blocks {@link List<SobiBlock>}
     * @return {@link List<SobiBlock>}
     */
    public List<SobiBlock> filterSobiBlocks(List<SobiBlock> blocks) {
        return blocks.stream()
                .filter(this::acceptBlock)
                .toList();
    }

    /**
     * Get the {@link SourceFilter} applicable for the given time.
     */
    private SourceFilter getApplicableFilter(LocalDateTime publishedDateTime) {
        SourceFilter applicableFilter = filterRangeMap.get(publishedDateTime);
        if (applicableFilter == null) {
            throw new IllegalStateException("No applicable filter for time " + publishedDateTime);
        }
        return applicableFilter;
    }

    private boolean acceptFragment(LegDataFragment fragment) {
        SourceFilter applicableFilter = getApplicableFilter(fragment.getPublishedDateTime());
        return applicableFilter.acceptFragment(fragment);
    }

    private boolean acceptBlock(SobiBlock block) {
        SourceFilter applicableFilter = getApplicableFilter(block.getPublishedDateTime());
        return applicableFilter.acceptBlock(block);
    }
}
