package gov.nysenate.openleg.search.member;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import gov.nysenate.openleg.api.legislation.member.view.FullMemberView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticMemberSearchService extends IndexedSearchService<FullMember> implements MemberSearchService {
    protected final SearchDao<Integer, FullMemberView, FullMember> memberSearchDao;
    protected final MemberService memberDataService;

    @Autowired
    public ElasticMemberSearchService(SearchDao<Integer, FullMemberView, FullMember> memberSearchDao,
                                      MemberService memberDataService) {
        super(memberSearchDao);
        this.memberSearchDao = memberSearchDao;
        this.memberDataService = memberDataService;
        // Members are normally updated by direct SQL, so force an index rebuild every time.
        memberSearchDao.deleteIndex();
        memberSearchDao.createIndex();
    }

    @Override
    public SearchResults<Integer> searchMembers(SessionYear sessionYear, Chamber chamber, String sort, LimitOffset limOff)
            throws SearchException {
        return search(null, chamber, sessionYear, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchMembers(String queryStr, SessionYear sessionYear, String sort, LimitOffset limOff)
            throws SearchException {
        return search(queryStr, null, sessionYear, sort, limOff);
    }

    private SearchResults<Integer> search(String queryStr, Chamber chamber, SessionYear sessionYear, String sort, LimitOffset limOff)
            throws SearchException {
        var queryBuilder = new BoolQuery.Builder();
        if (queryStr != null) {
            queryBuilder.must(ElasticSearchServiceUtils.getStringQuery(queryStr)._toQuery());
        }
        if (chamber != null) {
            queryBuilder.must(
                    MatchQuery.of(b -> b.field("chamber").query(chamber.toString()))._toQuery()
            );
        }
        if (sessionYear != null) {
            queryBuilder.must(
                    ExistsQuery.of(eqb -> eqb.field("sessionShortNameMap." + sessionYear.year()))._toQuery()
            );
        }
        return memberSearchDao.searchForIds(queryBuilder.build(), sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        updateIndex(memberDataService.getAllFullMembers());
    }
}
