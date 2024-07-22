package gov.nysenate.openleg.search.committee;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.committee.CommitteeSessionId;
import gov.nysenate.openleg.legislation.committee.CommitteeVersionId;
import gov.nysenate.openleg.search.SearchResults;

import java.util.Collection;
import java.util.List;

public interface CommitteeSearchDao {

    SearchResults<CommitteeVersionId> searchCommittees(Query query, Query filter,
                                                       List<SortOptions> sort, LimitOffset limitOffset);

    void updateCommitteeIndex(CommitteeSessionId committeeSessionId);

    void updateCommitteeIndexBulk(Collection<CommitteeSessionId> sessionIds);

    void deleteCommitteeFromIndex(CommitteeSessionId committeeSessionId);
}
