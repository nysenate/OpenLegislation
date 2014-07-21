package gov.nysenate.openleg.service.sobi;

import gov.nysenate.openleg.dao.sobi.SobiFileDao;
import gov.nysenate.openleg.dao.sobi.SobiFragmentDao;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.CachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class CachedSobiDataService implements SobiDataService, CachingService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedSobiDataService.class);

    /** --- Dao Instances --- */

    @Autowired
    private SobiFileDao sobiFileDao;

    @Autowired
    private SobiFragmentDao sobiFragmentDao;

    @PostConstruct
    protected void init() {
        setupCaches();
    }

    @Override
    public void setupCaches() {

    }

    @Override
    public void evictCaches() {

    }

    /** --- Implemented Methods --- */

    @Override
    public SobiFile getSobiFile(String fileName) {
        return null;
    }

    @Override
    public Map<SobiFragmentType, SobiFragment> getSobiFragments(String sobiFileName) {
        return null;
    }

    @Override
    public SobiFragment getSobiFragment(String fragmentName) {
        return null;
    }
}
