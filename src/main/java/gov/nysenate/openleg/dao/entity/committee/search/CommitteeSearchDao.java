package gov.nysenate.openleg.dao.entity.committee.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;

public interface CommitteeSearchDao {

    public SearchResults<CommitteeId> searchCommittees(QueryBuilder query, FilterBuilder filter,
                                                String sort, LimitOffset limitOffset);

    public SearchResults<CommitteeId> searchCurrentCommittees(QueryBuilder query, FilterBuilder filter,
                                                String sort, LimitOffset limitOffset);

    public void updateCommitteeIndex();
}
