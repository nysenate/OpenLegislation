package gov.nysenate.openleg.service.base.search;

import com.google.common.base.Splitter;
import gov.nysenate.openleg.model.search.SearchException;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ElasticSearchServiceUtils {

    /**
     * Generates a list of elastic search sort parameters from a CSV string.  If no parameters are specified,
     *  a single score sort parameter is used.
     *
     * @param sort String
     * @return List<SortBuilder>
     */
    public static List<SortBuilder> extractSortBuilders(String sort) throws SearchException {
        List<SortBuilder> sortBuilders = new ArrayList<>();
        if (sort == null || sort.trim().isEmpty()) {
            sortBuilders.add(SortBuilders.scoreSort());
        }
        else {
            try {
                Map<String, String> sortMap =
                        Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(sort);
                sortMap.forEach((k, v) -> sortBuilders.add(
                        SortBuilders.fieldSort(k).order(org.elasticsearch.search.sort.SortOrder.valueOf(v.toUpperCase()))));
            } catch (IllegalArgumentException ex) {
                throw new SearchException("Invalid sort string: '" + sort + "'\n" +
                        "Must be comma separated list of searchField:(ASC|DESC) e.g. 'status.statusType:ASC,status.actionDate:DESC'");
            }
        }
        return sortBuilders;
    }
}
