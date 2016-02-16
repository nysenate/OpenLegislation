package gov.nysenate.openleg.dao.entity.committee.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.entity.CommitteeSessionId;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.List;

public interface CommitteeSearchDao {

    public SearchResults<CommitteeVersionId> searchCommittees(QueryBuilder query, FilterBuilder filter,
                                                              List<SortBuilder> sort, LimitOffset limitOffset);

    public void updateCommitteeIndex(CommitteeSessionId committeeSessionId);

    public void updateCommitteeIndexBulk(Collection<CommitteeSessionId> sessionIds);

    public void deleteCommitteeFromIndex(CommitteeSessionId committeeSessionId);
}
