package gov.nysenate.openleg.legislation.committee.dao;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.*;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.updates.committee.CommitteeUpdateEvent;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CachedCommitteeDataService extends CachingService<CommitteeSessionId, List<Committee>> implements CommitteeDataService {
    private static final Logger logger = LoggerFactory.getLogger(CachedCommitteeDataService.class);
    @SuppressWarnings("unchecked")
    private static final Class<List<Committee>> VALUE_CLASS = (Class<List<Committee>>)
            List.of(new Committee()).getClass();
    @Autowired
    private CommitteeDao committeeDao;

    /** --- Cache Management --- */

    @Override
    protected CacheType cacheType() {
        return CacheType.COMMITTEE;
    }

    @Override
    protected Class<CommitteeSessionId> keyClass() {
        return CommitteeSessionId.class;
    }

    @Override
    protected Class<List<Committee>> valueClass() {
        return VALUE_CLASS;
    }

    @Override
    protected CacheConfigurationBuilder<CommitteeSessionId, List<Committee>> getConfigBuilder() {
        return super.getConfigBuilder().withEvictionAdvisor(new CommitteeCacheEvictionPolicy());
    }

    // TODO: add this
    //committeeCache.setMemoryStoreEvictionPolicy(new CommitteeCacheEvictionPolicy());

    /** {@inheritDoc} */
    @Override
    public void warmCaches() {
        evictCaches();
        logger.info("Warming up committee cache.");
        getCommitteeList(Chamber.SENATE, LimitOffset.ALL);
        getCommitteeList(Chamber.ASSEMBLY, LimitOffset.ALL);
        logger.info("Done warming up committee cache.");
    }

    /** --- Committee Data Services --- */

    /**
     * {@inheritDoc}
     * @param committeeSessionId
     */
    @Override
    public Committee getCommittee(CommitteeSessionId committeeSessionId) throws CommitteeNotFoundEx {
        if (committeeSessionId == null)
            throw new IllegalArgumentException("committeeSessionId cannot be null!");
        try {
            return getCommitteeHistory(committeeSessionId).get(0);
        } catch (IndexOutOfBoundsException ex) {
            throw new CommitteeNotFoundEx(committeeSessionId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Committee getCommittee(CommitteeVersionId committeeVersionId) throws CommitteeNotFoundEx {
        if (committeeVersionId == null)
            throw new IllegalArgumentException("committeeVersionId cannot be null!");
        LocalDateTime refDate = committeeVersionId.getReferenceDate();
        for (Committee committee : getCommitteeHistory(committeeVersionId)) {
            if ( committee.getCreated().equals(refDate) ||
                     committee.getCreated().isBefore(refDate)  &&
                    (committee.getReformed()==null || committee.getReformed().isAfter(refDate)) ) {
                return committee;
            }
        }
        throw new CommitteeNotFoundEx(committeeVersionId, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommitteeId> getCommitteeIds() {
        return committeeDao.getCommitteeList();
    }

    @Override
    public List<CommitteeSessionId> getAllCommitteeSessionIds() {
        return committeeDao.getAllSessionIds();
    }

    /** {@inheritDoc} */
    @Override
    public List<Committee> getCommitteeList(Chamber chamber, SessionYear sessionYear, LimitOffset limitOffset) {
        if (chamber == null)
            throw new IllegalArgumentException("Chamber cannot be null!");

        List<Committee> committeeList = new ArrayList<>();
        getCommitteeIds().stream()
                .filter(committeeId -> committeeId.getChamber().equals(chamber))
                .map(committeeId -> new CommitteeSessionId(committeeId, sessionYear))
                .forEach(committeeSessionId -> {
                    try {
                        committeeList.add(getCommittee(committeeSessionId));
                    } catch (CommitteeNotFoundEx ignored) {}
                });

        return LimitOffset.limitList(committeeList, limitOffset != null ? limitOffset : LimitOffset.ALL);
    }

    /** {@inheritDoc} */
    @Override
    public int getCommitteeListCount(Chamber chamber, SessionYear sessionYear) {
        return getCommitteeList(chamber, sessionYear, LimitOffset.ALL).size();
    }

    /** {@inheritDoc} */
    @Override
    public List<Committee> getCommitteeHistory(CommitteeSessionId committeeSessionId,
                                               LimitOffset limitOffset, SortOrder order) throws CommitteeNotFoundEx {
        if (committeeSessionId ==null)
            throw new IllegalArgumentException("CommitteeSessionId cannot be null!");

        List<Committee> committeeHistory = cache.get(committeeSessionId);
        if (committeeHistory != null)
            logger.debug("Committee cache hit for {}", committeeSessionId);
        else {
            try {
                committeeHistory = committeeDao.getCommitteeHistory(committeeSessionId);
                cache.put(committeeSessionId, committeeHistory);
                logger.debug("Added committee history {} to cache", committeeSessionId);
            }
            catch (EmptyResultDataAccessException ex){
                throw new CommitteeNotFoundEx(committeeSessionId, ex);
            }
        }

        // The dao provides the result already in DESC order by created date
        if (order != null && order.equals(SortOrder.ASC))
            committeeHistory = Lists.reverse(committeeHistory);
        if (limitOffset != null && !limitOffset.equals(LimitOffset.ALL))
            committeeHistory = LimitOffset.limitList(committeeHistory, limitOffset);
        return committeeHistory;
    }

    /** {@inheritDoc}
     * @param committeeSessionId*/
    @Override
    public int getCommitteeHistoryCount(CommitteeSessionId committeeSessionId) {
        try {
            return getCommitteeHistory(committeeSessionId).size();
        } catch (CommitteeNotFoundEx ex) {
            return 0;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveCommittee(Committee committee, LegDataFragment legDataFragment) {
        if (committee == null)
            throw new IllegalArgumentException("Committee cannot be null.");
        // Update the database.
        committeeDao.updateCommittee(committee, legDataFragment);

        // Update the cache.
        List<Committee> committeeHistory = committeeDao.getCommitteeHistory(committee.getSessionId());
        cache.put(committee.getSessionId(), committeeHistory);

        eventBus.post(new CommitteeUpdateEvent(committee, LocalDateTime.now()));
    }

}
