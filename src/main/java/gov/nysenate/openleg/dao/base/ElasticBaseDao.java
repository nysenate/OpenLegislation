package gov.nysenate.openleg.dao.base;

import com.google.common.base.Splitter;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.sort.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for Elastic Search layer classes  to inherit common functionality from.
 */
public abstract class ElasticBaseDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBaseDao.class);

    @Autowired
    protected Client searchClient;

    @PostConstruct
    private void init() {}

    /** --- Common Elastic Search methods --- */

    public List<SortBuilder> extractSortFilters(String sort) {
        List<SortBuilder> sortBuilders = new ArrayList<>();
        if (sort == null || sort.trim().isEmpty()) {
            sortBuilders.add(SortBuilders.scoreSort());
        }
        else {
            Map<String, String> sortMap =
                Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(sort);
            sortMap.forEach((k,v) -> sortBuilders.add(
                SortBuilders.fieldSort(k).order(org.elasticsearch.search.sort.SortOrder.valueOf(v.toUpperCase()))));
        }
        return sortBuilders;
    }
}
