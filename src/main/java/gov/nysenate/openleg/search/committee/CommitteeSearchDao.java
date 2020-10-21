package gov.nysenate.openleg.search.committee;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.committee.CommitteeSessionId;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import gov.nysenate.openleg.search.SearchResults;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.List;

public interface CommitteeSearchDao {

    SearchResults<CommitteeVersionId> searchCommittees(QueryBuilder query, QueryBuilder filter,
                                                              List<SortBuilder<?>> sort, LimitOffset limitOffset);

    void updateCommitteeIndex(CommitteeSessionId committeeSessionId);

    void updateCommitteeIndexBulk(Collection<CommitteeSessionId> sessionIds);

    void deleteCommitteeFromIndex(CommitteeSessionId committeeSessionId);
}
