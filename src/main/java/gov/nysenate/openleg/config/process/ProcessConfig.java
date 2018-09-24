package gov.nysenate.openleg.config.process;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessConfig {

    private final RangeMap<Integer, SourceFilter> filterRangeMap;
    private final SourceFilter xmlFilter = new XmlWhitelistFilter();
    private final SourceFilter sobiFilter = new SobiWhitelistFilter();

    public ProcessConfig() {
        filterRangeMap = ImmutableRangeMap.<Integer, SourceFilter>builder()
                .put(Range.lessThan(2009), xmlFilter)
                .put(Range.closedOpen(2009, 2018), sobiFilter)
                .put(Range.atLeast(2018), xmlFilter)
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
        return fragments.stream()
                .filter(this::acceptFragment)
                .collect(Collectors.toList());
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
                .filter(sobiFilter::acceptBlock)
                .collect(Collectors.toList());
    }

    private boolean acceptFragment(SobiFragment fragment) {
        SourceFilter applicableFilter = filterRangeMap.get(fragment.getPublishedDateTime().getYear());
        if (applicableFilter == null) {
            throw new IllegalStateException("No applicable filter for fragment " + fragment);
        }
        return applicableFilter.acceptFragment(fragment);
    }
}
