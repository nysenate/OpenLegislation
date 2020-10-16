package gov.nysenate.openleg.service.base.search;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.model.search.SearchException;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ElasticSearchServiceUtils {

    // Sorting scores and fields requires different method calls,
    // so the correct type must be identified.
    private final static String SCORE_NAME = "_score";

    private static final ImmutableSet<String> commonTextSortFields = ImmutableSet.of(
            "printNo",
            "basePrintNo",
            "basePrintNoStr",
            "version",
            "activeVersion",
            "chamber",
            "location",
            "name",
            "docLevelId",
            "docType",
            "lawId",
            "lawName",
            "locationId",
            "email",
            "shortName",
            "fullName",
            "prefix",
            "firstName",
            "middleName",
            "lastName",
            "suffix",
            "imgName",
            "notificationType",
            "filename",
            "location",
            "sessionType"
    );

    /**
     * Generates a list of elastic search sort parameters from a CSV string.  If no parameters are specified,
     * a single score sort parameter is used.
     *
     * @param sort String
     * @return List<SortBuilder>
     */
    public static List<SortBuilder<?>> extractSortBuilders(String sort) throws SearchException {
        List<SortBuilder<?>> sortBuilders = new ArrayList<>();
        if (sort == null || sort.trim().isEmpty()) {
            sortBuilders.add(SortBuilders.scoreSort());
        }
        else {
            try {
                Map<String, String> sortMap =
                        Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(sort);
                sortMap.forEach((field, order) -> {
                    // Replace common text properties with their keyword field
                    // This is to maintain some limited backwards compatibility with the API before the elasticsearch 6 upgrade
                    if (commonTextSortFields.contains(field)) {
                        field += ".keyword";
                    }
                    SortBuilder<?> sb = SCORE_NAME.equals(field)
                            ? SortBuilders.scoreSort() : SortBuilders.fieldSort(field);
                    sb.order(org.elasticsearch.search.sort.SortOrder.valueOf(StringUtils.upperCase(order)));
                    sortBuilders.add(sb);
                });
            } catch (IllegalArgumentException ex) {
                throw new SearchException("Invalid sort string: '" + sort + "'\n" +
                        "Must be comma separated list of searchField:(ASC|DESC) e.g. 'status.statusType:ASC,status.actionDate:DESC'");
            }
        }
        return sortBuilders;
    }
}
