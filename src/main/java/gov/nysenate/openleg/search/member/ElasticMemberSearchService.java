package gov.nysenate.openleg.search.member;

import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.api.legislation.member.view.FullMemberView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class ElasticMemberSearchService implements MemberSearchService, IndexedSearchService<FullMember> {
    private static final Logger logger = LoggerFactory.getLogger(ElasticMemberSearchService.class);

    protected final OpenLegEnvironment env;
    protected final SearchDao<Integer, FullMemberView, FullMember> memberSearchDao;
    protected final MemberService memberDataService;

    @Autowired
    public ElasticMemberSearchService(OpenLegEnvironment env,
                                      SearchDao<Integer, FullMemberView, FullMember> memberSearchDao,
                                      MemberService memberDataService, EventBus eventBus) {
        this.env = env;
        this.memberSearchDao = memberSearchDao;
        this.memberDataService = memberDataService;
        eventBus.register(this);
        this.rebuildIndex();
    }

    @Override
    public SearchResults<Integer> searchMembers(SessionYear sessionYear, Chamber chamber, String sort, LimitOffset limOff)
            throws SearchException {
        Query chamberQuery = TermQuery.of(b -> b.field("chamber").value(chamber.toString().toLowerCase()))._toQuery();
        return search(
                QueryBuilders.bool(b -> b.must(sessionYearExistsQuery(sessionYear), chamberQuery)),
                sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchMembers(String query, SessionYear sessionYear, String sort, LimitOffset limOff)
            throws SearchException {
        Query esQuery = IndexedSearchService.getStringQuery(query);
        return search(
                QueryBuilders.bool(b -> b.must(sessionYearExistsQuery(sessionYear), esQuery)),
                sort, limOff);
    }

    private SearchResults<Integer> search(Query query, String sort, LimitOffset limOff)
            throws SearchException {
        if (limOff == null) {
            // TODO: some map or function to get a default for everything?
            limOff = LimitOffset.TWENTY_FIVE;
        }
        return memberSearchDao.searchForIds(query,
                ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(FullMember member) {
        if (env.isElasticIndexing() && member != null) {
            logger.info("Indexing member {} into elastic search.", member.getPerson().name().lastName());
            memberSearchDao.updateIndex(member);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<FullMember> members) {
        if (env.isElasticIndexing() && !members.isEmpty()) {
            List<FullMember> indexableMembers = members.stream().filter(Objects::nonNull).toList();
            logger.info("Indexing {} valid members into elastic search.", indexableMembers.size());
            memberSearchDao.updateIndex(indexableMembers);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clearIndex() {
        memberSearchDao.purgeIndices();
        memberSearchDao.createIndices();
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        clearIndex();
        List<FullMember> members = memberDataService.getAllFullMembers();
        logger.info("Indexing {} members", members.size());
        updateIndex(members);
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.MEMBER)) {
            logger.info("Handling member re-index event");
            rebuildIndex();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.MEMBER)) {
            clearIndex();
        }
    }

    /**
     * Generate a query that matches members that were active on the given session year.
     * @param sessionYear {@link SessionYear}
     * @return QueryBuilder
     */
    private static Query sessionYearExistsQuery(SessionYear sessionYear) {
        return (sessionYear == null ? QueryBuilders.matchAll().build() :
                ExistsQuery.of(eqb -> eqb.field("sessionShortNameMap." + sessionYear.year())))._toQuery();
    }
}
