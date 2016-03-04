package gov.nysenate.openleg.client.view.spotcheck;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.*;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SpotCheckSummaryView implements ViewObject {

    protected Map<SpotCheckMismatchStatus, Long> mismatchStatuses;
    protected Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus,
            Map<SpotCheckMismatchIgnore, Map<SpotCheckMismatchTracked, Long>>>> mismatchCounts;

    public SpotCheckSummaryView(SpotCheckSummary summary) {
        this.mismatchStatuses = summary.getMismatchStatuses();
        this.mismatchCounts = summary.getMismatchCounts().cellSet().stream()
                .collect(Collectors.toMap(Table.Cell::getRowKey,
                        cell -> ImmutableMap.of(cell.getColumnKey(), cell.getValue().rowMap()),
                        (map1, map2) -> {
                            Map<SpotCheckMismatchStatus, Map<SpotCheckMismatchIgnore,
                                    Map<SpotCheckMismatchTracked, Long>>> mergedMap = new HashMap<>(map1);
                            mergedMap.putAll(map2);
                            return mergedMap;
                        }));
    }

    public Long getOpenMismatches() {
        return mismatchStatuses.entrySet().stream()
                .filter(e -> !e.getKey().equals(SpotCheckMismatchStatus.RESOLVED))
                .map(Map.Entry::getValue).reduce(Long::sum).orElse(0L);
    }

    public Map<SpotCheckMismatchStatus, Long> getMismatchStatuses() {
        return mismatchStatuses;
    }

    public Map<SpotCheckMismatchType, Map<SpotCheckMismatchStatus,
            Map<SpotCheckMismatchIgnore, Map<SpotCheckMismatchTracked, Long>>>> getMismatchCounts() {
        return mismatchCounts;
    }

    @Override
    public String getViewType() {
        return "spotcheck-summary";
    }
}
