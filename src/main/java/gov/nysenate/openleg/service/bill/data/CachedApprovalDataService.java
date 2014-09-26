package gov.nysenate.openleg.service.bill.data;

import gov.nysenate.openleg.dao.bill.ApprovalDao;
import gov.nysenate.openleg.model.bill.ApprovalId;
import gov.nysenate.openleg.model.bill.ApprovalMessage;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CachedApprovalDataService implements ApprovalDataService, CachingService {

    public static final Logger logger = LoggerFactory.getLogger(CachedApprovalDataService.class);

    public static final String approvalDataCache = "approvalData";
    
    public static final String approvalIdKey = "approvalId";
    public static final String billIdKey = "billId";
    public static final String yearIdKey = "year";

    @Autowired
    CacheManager cacheManager;

    @Autowired
    ApprovalDao approvalDao;

    @PostConstruct
    public void setup(){
        setupCaches();
    }

    /**{@inheritDoc}*/
    @Override
    public void setupCaches() {
        cacheManager.addCache(approvalDataCache);
    }

    /**{@inheritDoc}*/
    @Override
    @CacheEvict(value = approvalDataCache, allEntries = true)
    public void evictCaches() {}

    /**{@inheritDoc}*/
    @Override
    @Cacheable(value = approvalDataCache, key = "#approvalIdKey + '-' + #approvalId.toString()")
    public ApprovalMessage getApprovalMessage(ApprovalId approvalId) throws ApprovalNotFoundException {
        try{
            return approvalDao.getApprovalMessage(approvalId);
        }
        catch(DataAccessException ex){
            throw new ApprovalNotFoundException(ex, approvalId);
        }
    }

    /**{@inheritDoc}*/
    @Override
    @Cacheable(value = approvalDataCache, key = "#billIdKey + '-' + #baseBillId.toString()")
    public ApprovalMessage getApprovalMessage(BaseBillId baseBillId) throws ApprovalNotFoundException {
        try{
            return approvalDao.getApprovalMessage(baseBillId);
        }
        catch(DataAccessException ex){
            throw new ApprovalNotFoundException(ex, baseBillId);
        }
    }

    /**{@inheritDoc}*/
    @Override
    @Cacheable(value = approvalDataCache, key = "#yearIdKey + '-' + #year")
    public List<ApprovalMessage> getApprovalMessages(int year) throws ApprovalNotFoundException {
        try{
            return approvalDao.getApprovalMessages(year);
        }
        catch(DataAccessException ex){
            throw new ApprovalNotFoundException(ex, year);
        }
    }

    /**{@inheritDoc}*/
    @Override
    @CacheEvict(value = approvalDataCache, allEntries = true)
    public void updateApprovalMessage(ApprovalMessage approvalMessage, SobiFragment sobiFragment) {
        approvalDao.updateApprovalMessage(approvalMessage, sobiFragment);
    }

    /**{@inheritDoc}*/
    @Override
    @CacheEvict(value = approvalDataCache, allEntries = true)
    public void deleteApprovalMessage(ApprovalId approvalId) {
        approvalDao.deleteApprovalMessage(approvalId);
    }

    /**{@inheritDoc}*/
    @Override
    @CacheEvict(value = approvalDataCache, allEntries = true)
    public void deleteApprovalMessage(BaseBillId baseBillId) {
        approvalDao.deleteApprovalMessage(baseBillId);
    }
}
