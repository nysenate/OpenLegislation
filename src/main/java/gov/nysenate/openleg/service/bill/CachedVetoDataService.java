package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.dao.bill.VetoDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.VetoMessage;
import gov.nysenate.openleg.model.bill.VetoId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.base.CachingService;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
public class CachedVetoDataService implements VetoDataService, CachingService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedVetoDataService.class);

    private static final String vetoDataCache = "vetoData";

    private static final String getVetoKey = "getVetoMessage";
    private static final String getBillVetoesKey = "getBillVetoes";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private VetoDao vetoDao;

    @PostConstruct
    private void init(){ setupCaches(); }

    @Override
    public void setupCaches() {
        cacheManager.addCache(vetoDataCache);
    }

    @Override
    @CacheEvict(value = vetoDataCache, allEntries = true)
    public void evictCaches() {}

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = vetoDataCache, key = "#getVetoKey + '-' + #vetoId.toString()")
    public VetoMessage getVetoMessage(VetoId vetoId) throws VetoNotFoundException {
        if(vetoId==null){
            throw new IllegalArgumentException("vetoId cannot be null!");
        }
        try{
            return vetoDao.getVetoMessage(vetoId);
        }
        catch(EmptyResultDataAccessException ex){
            throw new VetoNotFoundException(ex, vetoId);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = vetoDataCache, key = "#getBillVetoesKey + '-' + #baseBillId.toString()")
    public Map<VetoId,VetoMessage> getBillVetoes(BaseBillId baseBillId) throws VetoNotFoundException {
        if(baseBillId==null){
            throw new IllegalArgumentException("baseBillId cannot be null!");
        }
        try{
            return vetoDao.getBillVetoes(baseBillId);
        }
        catch(EmptyResultDataAccessException ex){
            throw new VetoNotFoundException(ex, baseBillId);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Caching( evict = {
            @CacheEvict(value = vetoDataCache, key = "#getVetoKey + '-' + #vetoMessage.getVetoId().toString()"),
            @CacheEvict(value = vetoDataCache, key = "#getBillVetoesKey + '-' + #vetoMessage.getBillId().toString()")
    })
    public void updateVetoMessage(VetoMessage vetoMessage, SobiFragment sobiFragment) {
        vetoDao.updateVetoMessage(vetoMessage, sobiFragment);
    }
}
