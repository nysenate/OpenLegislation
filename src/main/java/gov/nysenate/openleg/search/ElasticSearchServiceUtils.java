package gov.nysenate.openleg.search;

import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public final class ElasticSearchServiceUtils {
    private ElasticSearchServiceUtils() {}

    // Sorting scores and fields requires different method calls,
    // so the correct type must be identified.
    private static final String SCORE_NAME = "_score";

    private static final ImmutableSet<String> commonTextSortFields = ImmutableSet.of(
            "printNo", "basePrintNo", "basePrintNoStr", "version", "activeVersion", "chamber",
            "location", "name",
            "docLevelId", "docType", "lawId", "lawName",
            "locationId", "email",
            "shortName", "fullName", "prefix", "firstName", "middleName", "lastName", "suffix",
            "imgName", "notificationType", "filename", "sessionType"
    );

    /**
     * Generates a list of elastic search sort parameters from a CSV string. If no parameters are specified,
     * a single score sort parameter is used.
     *
     * @param sort String
     * @return List<SortBuilder>
     */
    public static List<SortOptions> extractSortBuilders(String sort) throws SearchException {
        var sortBuilders = new ArrayList<SortOptions>();
        if (sort == null || sort.trim().isEmpty()) {
            sortBuilders.add(ScoreSort.of(b -> b)._toSortOptions());
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
                    var sortOrder = SortOrder.valueOf(StringUtils.capitalize(order.toLowerCase()));
                    if (SCORE_NAME.equals(field)) {
                        sortBuilders.add(SortOptionsBuilders.score(b -> b.order(sortOrder)));
                    }
                    else {
                        final String finalField = field;
                        sortBuilders.add(SortOptionsBuilders.field(b -> b.field(finalField).order(sortOrder)));
                    }
                });
            } catch (IllegalArgumentException ex) {
                throw new SearchException("Invalid sort string: '" + sort + "'\n" +
                        "Must be comma separated list of searchField:(ASC|DESC) e.g. 'status.statusType:ASC,status.actionDate:DESC'");
            }
        }
        return sortBuilders;
    }

    // TODO: make use of something similar to SearchParseException
    public static QueryVariant getStringQuery(String query) {
        if (query == null) {
            return null;
        }
        return QueryStringQuery.of(b -> b.query(query));
    }
}
