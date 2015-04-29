package gov.nysenate.openleg.service.entity.member.search;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.entity.member.search.ElasticMemberSearchDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.search.ClearIndexEvent;
import gov.nysenate.openleg.model.search.RebuildIndexEvent;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.service.entity.member.event.BulkMemberUpdateEvent;
import gov.nysenate.openleg.service.entity.member.event.MemberUpdateEvent;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
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
import java.util.stream.Collectors;

@Service
public class ElasticMemberSearchService implements MemberSearchService, IndexedSearchService<Member>
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticMemberSearchService.class);

    @Autowired protected Environment env;
    @Autowired protected EventBus eventBus;
    @Autowired protected ElasticMemberSearchDao memberSearchDao;
    @Autowired protected MemberService memberDataService;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<Member> searchMembers(SessionYear sessionYear, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
                FilterBuilders.termFilter("sessionYear", sessionYear.getYear())), null, sort, limOff);
    }

    @Override
    public SearchResults<Member> searchMembers(SessionYear sessionYear, Chamber chamber, String sort, LimitOffset limOff) throws SearchException {
        String query = "(chamber:" + chamber.toString() + ") AND (sessionYear:" + sessionYear.getYear() + ")";
        return search(QueryBuilders.queryString(query), null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<Member> searchMembers(String query, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.queryString(query), null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<Member> searchMembers(String query, SessionYear sessionYear, String sort, LimitOffset limOff) throws SearchException {
        return search(QueryBuilders.filteredQuery(QueryBuilders.queryString(query), FilterBuilders.termFilter("sessionYear", sessionYear.getYear())), null, sort, limOff);
    }

    private SearchResults<Member> search(QueryBuilder query, FilterBuilder postFilter, String sort, LimitOffset limOff)
            throws SearchException {
        if (limOff == null) limOff = LimitOffset.TWENTY_FIVE;
        try {
            return memberSearchDao.searchMembers(query, postFilter, sort, limOff);
        }
        catch (SearchParseException ex) {
            throw new SearchException("Invalid query string", ex);
        }
        catch (ElasticsearchException ex) {
            throw new SearchException("Unexpected search exception!", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Member member) {
        if (env.isElasticIndexing() && member != null) {
            logger.info("Indexing member {} into elastic search.", member.getLbdcShortName());
            memberSearchDao.updateMemberIndex(member);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<Member> members) {
        if (env.isElasticIndexing() && !members.isEmpty()) {
            List<Member> indexableMembers = members.stream().filter(t -> t != null).collect(Collectors.toList());
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
        LimitOffset limOff = LimitOffset.HUNDRED;
        SortOrder sortOrder = SortOrder.ASC;
        List<Member> members;
        do {
            members = memberDataService.getAllMembers(sortOrder, limOff);
            logger.info("Indexing {} members", members.size());
            updateIndex(members);
            limOff = limOff.next();
            members = memberDataService.getAllMembers(sortOrder, limOff);
        }
        while(!members.isEmpty());
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.MEMBER)) {
            logger.info("Handling member re-index event");
            try {
                rebuildIndex();
            } catch (Exception ex) {
                logger.error("Unexpected exception during handling of member index rebuild event.", ex);
            }
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

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleMemberUpdate(MemberUpdateEvent memberUpdateEvent) {
        if (memberUpdateEvent.getMember() != null) {
            updateIndex(memberUpdateEvent.getMember());
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleBulkMemberUpdate(BulkMemberUpdateEvent bulkMemberUpdateEvent) {
        if (bulkMemberUpdateEvent.getMembers() != null) {
            updateIndex(bulkMemberUpdateEvent.getMembers());
        }
    }
}
