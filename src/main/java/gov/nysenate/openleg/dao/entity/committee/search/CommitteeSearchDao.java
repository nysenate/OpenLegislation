package gov.nysenate.openleg.dao.entity.committee.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.entity.CommitteeSessionId;
import gov.nysenate.openleg.model.entity.CommitteeVersionId;
import gov.nysenate.openleg.model.search.SearchResults;
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
