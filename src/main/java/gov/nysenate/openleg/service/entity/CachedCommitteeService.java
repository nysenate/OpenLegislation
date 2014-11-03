package gov.nysenate.openleg.service.entity;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.entity.CommitteeDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.util.DateUtils;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CachedCommitteeService implements CommitteeService{

    private static final Logger logger = LoggerFactory.getLogger(CachedCommitteeService.class);

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private CommitteeDao committeeDao;

    @PostConstruct
    private void init(){
        cacheManager.addCache("committee");
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "committee", key = "#root.methodName + '-' + #committeeId.toString()")
    public Committee getCommittee(CommitteeId committeeId) throws CommitteeNotFoundEx {
        if (committeeId == null) {
            throw new IllegalArgumentException("CommitteeId cannot be null!");
        }
        try{
            return committeeDao.getCommittee(committeeId);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new CommitteeNotFoundEx(committeeId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "committee",
               key = "#root.methodName + '-' + #name + '-' + #committeeVersionId.toString()")
    public Committee getCommittee(CommitteeVersionId committeeVersionId) throws CommitteeNotFoundEx {
        if (committeeVersionId == null) {
            throw new IllegalArgumentException("committeeVersionId cannot be null!");
        }
        try {
            return committeeDao.getCommittee(committeeVersionId);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new CommitteeNotFoundEx(committeeVersionId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "committee", key = "#root.methodName + '-' + #chamber.toString() + '-' + #limitOffset.toString()")
    public List<Committee> getCommitteeList(Chamber chamber, LimitOffset limitOffset) {
        if (chamber == null) {
            throw new IllegalArgumentException("Chamber cannot be null!");
        }
        return committeeDao.getCommitteeList(chamber, limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "committee", key = "#root.methodName + '-' + #chamber.toString()")
    public int getCommitteeListCount(Chamber chamber) {
        return committeeDao.getCommitteeListCount(chamber);
    }

    /** {@inheritDoc} */
    @Override
//    TODO figure out date range caching
//    @Cacheable(value = "committee", key = "#root.methodName + '-' + #committeeId.toString().toLowerCase() + '-' + #limitOffset.toString() + '-' + #order.name()")
    public List<Committee> getCommitteeHistory(CommitteeId committeeId, Range<LocalDateTime> dateRange,
                                               LimitOffset limitOffset, SortOrder order) throws CommitteeNotFoundEx {
        if (committeeId==null) {
            throw new IllegalArgumentException("CommitteeId cannot be null!");
        }

        try {
            return committeeDao.getCommitteeHistory(committeeId, dateRange, limitOffset, order);
        }
        catch (EmptyResultDataAccessException ex){
            throw new CommitteeNotFoundEx(committeeId, dateRange, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
//    TODO figure out date range caching
//    @Cacheable(value = "committee", key = "#root.methodName + '-' + #committeeId.toString().toLowerCase()")
    public int getCommitteeHistoryCount(CommitteeId committeeId, Range<LocalDateTime> dateRange) {
        return committeeDao.getCommitteeHistoryCount(committeeId, dateRange);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = "committee", allEntries = true)
    public void updateCommittee(Committee committee) {
        if(committee==null) {
            throw new IllegalArgumentException("Committee cannot be null.");
        }
        committeeDao.updateCommittee(committee);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = "committee", allEntries = true)
    public void deleteCommittee(CommitteeId committeeId) {
        if(committeeId==null) {
            throw new IllegalArgumentException("CommitteeId cannot be null!");
        }
        committeeDao.deleteCommittee(committeeId);
    }
}
