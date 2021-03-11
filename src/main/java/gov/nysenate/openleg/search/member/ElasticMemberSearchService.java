package gov.nysenate.openleg.search.member;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.member.FullMember;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.search.*;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ElasticMemberSearchService implements MemberSearchService, IndexedSearchService<FullMember>
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticMemberSearchService.class);

    @Autowired protected Environment env;
    @Autowired protected EventBus eventBus;
    @Autowired protected ElasticMemberSearchDao memberSearchDao;
    @Autowired protected MemberService memberDataService;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
        this.rebuildIndex();
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchMembers(SessionYear sessionYear, String sort, LimitOffset limOff) throws SearchException {
        return search(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchAllQuery())
                        .filter(requireSessionYear(sessionYear)),
                null, sort, limOff);
    }

    @Override
    public SearchResults<Integer> searchMembers(SessionYear sessionYear, Chamber chamber, String sort, LimitOffset limOff) throws SearchException {
        return search(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchAllQuery())
                        .filter(requireSessionYear(sessionYear))
                        .filter(QueryBuilders.termQuery("chamber", chamber.toString().toLowerCase())),
                null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchMembers(String query, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.queryStringQuery(query), null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<Integer> searchMembers(String query, SessionYear sessionYear, String sort, LimitOffset limOff) throws SearchException {
        return search(
                QueryBuilders.boolQuery()
                        .filter(requireSessionYear(sessionYear))
                        .must(QueryBuilders.queryStringQuery(query)),
                null, sort, limOff);
    }

    private SearchResults<Integer> search(QueryBuilder query, QueryBuilder postFilter, String sort, LimitOffset limOff)
            throws SearchException {
        if (limOff == null) limOff = LimitOffset.TWENTY_FIVE;
        try {
            return memberSearchDao.searchMembers(query, postFilter,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
        }
        catch (SearchParseException ex) {
            throw new SearchException("Invalid query string", ex);
        }
        catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex.getMessage(), ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(FullMember member) {
        if (env.isElasticIndexing() && member != null) {
            logger.info("Indexing member {} into elastic search.", member.getFullName());
            memberSearchDao.updateMemberIndex(member);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<FullMember> members) {
        if (env.isElasticIndexing() && !members.isEmpty()) {
            List<FullMember> indexableMembers = members.stream().filter(Objects::nonNull).collect(Collectors.toList());
            logger.info("Indexing {} valid members into elastic search.", indexableMembers.size());
            memberSearchDao.updateMemberIndex(indexableMembers);
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

    /* --- Internal Methods --- */

    private void updateSessionMember(SessionMember sessionMember) {
        FullMember member = memberDataService.getFullMemberById(sessionMember.getMember().getMemberId());
        updateIndex(member);
    }

    /**
     * Generate a query that matches members that were active on the given session year.
     *
     * @param sessionYear {@link SessionYear}
     * @return QueryBuilder
     */
    private QueryBuilder requireSessionYear(SessionYear sessionYear) {
        return QueryBuilders.existsQuery("sessionShortNameMap." + sessionYear.getYear());
    }
}